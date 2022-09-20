package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.*;
import java.util.Vector;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) throws IOException, InterruptedException {

        Cluster cluster = Cluster.getInstance();
        String path = args[0];
        JsonInput newInput = new Gson().fromJson(new FileReader(path), JsonInput.class);
        Student [] students = newInput.getStudents();
        ConfrenceInformation[] c = newInput.getConfrenceInformations();
        Vector<GPU> gpus = new Vector<>();
        for(String gpu : newInput.getGpus()){
            gpus.add(new GPU(gpu));
        }
        Vector<CPU> cpus = new Vector<>();
        for(int cpu : newInput.getCpus()){
            cpus.add(new CPU(cpu));
        }
        Cluster.getInstance().setGpus(gpus);
        Cluster.getInstance().setCpus(cpus);
        Vector<Thread> msThreads= new Vector<>();
        Vector<Thread> GpuThreads = new Vector<>();
        for(GPU gpu : gpus) {
            Thread GpuThread = new Thread(new GPUService("GpuService", gpu));
            GpuThread.start();
            GpuThreads.add(GpuThread);
        }
        for(CPU cpu : cpus) {
            Thread CpuThread = new Thread(new CPUService("CpuService", cpu));
            msThreads.add(CpuThread);
        }
        for (ConfrenceInformation con: c) {
            con.setModelsToPublish();
            Thread ConfrenceThread = new Thread(new ConferenceService(con.getName(),con));
            msThreads.add(ConfrenceThread);
        }
        for(Student s : students) {
            s.setTrainedModels();
            for(Model model: s.getModels()){
                model.setData();
                model.setStudent(s);
                model.setPublished(false);
            }
            Thread StudentThread = new Thread(new StudentService(s.getStudentName(), s));
            msThreads.add(StudentThread);
        }
        TimeService ts = new TimeService(newInput.getDuration(), newInput.getTickTime());
        Thread timeThread = new Thread(ts);

        for(Thread t: msThreads){
            t.start();
        }
        timeThread.start();
        timeThread.join();

        for(Thread t: GpuThreads){
            t.join();
        }
        for(Thread t: msThreads){
            t.join();
        }






        PrintWriter output = new PrintWriter("output");

        output.println("Students:");
        for(Student student : students){
            output.println("    Student name: " + student.getStudentName() );
            output.println("    Student department: " + student.getDepartment());
            output.println("    Student status: " + student.getStudentDegree());
            output.println("    Student publications: " + student.getPublications());
            output.println("    Student papersRead: " + student.getPapersRead());
            output.println("    Trained models:");
            output.println();
            for(Model model : student.getTrainedModels()){
                output.println("        Name: " + model.getName());
                output.println("        Data:");
                output.println("            Type: " + model.getData().getType());
                output.println("            size: " + model.getData().getSize());
                output.println("        Status: " + model.getStatus() );
                output.println("        Result: " + model.getCurrResult() );
                output.println("        Published: " + model.isPublished());
                output.println();
            }
            output.println();
        }
        output.println("Conferences:");
        for (ConfrenceInformation con : c){
            output.println("    Conference name: " + con.getName());
            output.println("    Conference date: " + con.getDate());
            output.println("    Publications:");
            for(Model model : con.getModelsToPublish()){
                if(model.isPublished()) {
                    output.println("        Name: " + model.getName());
                    output.println("        Data:");
                    output.println("            Type: " + model.getData().getType());
                    output.println("            Size: " + model.getData().getSize());
                    output.println("        Status: " + model.getStatus());
                    output.println("        Result: " + model.getCurrResult());
                    output.println();
                }
            }
            output.println();
        }
        output.println("CPU time used: " + cluster.getCpuTime());
        output.println("GPU time used: " + cluster.getGpuTime());
        output.println("Batches processed: " + cluster.getBatchesProcessed());

        output.close();

    }
}
