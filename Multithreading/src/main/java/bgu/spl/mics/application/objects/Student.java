package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private Model[] models;
    private int modelSent;
    private LinkedList<Model> trainedModels;

    public Student (String name, String department, String status, Model[] models){
        this.name = name;
        this.department = department;
        if(status.equals("Msc"))
            this.status = Degree.MSc;
        else
            this.status = Degree.PhD;
        this.models = models;
        modelSent = 0;
        publications = 0;
        papersRead = 0;
    }

    public String getStudentName(){return name;}

    public void addTrainedModel(Model model){trainedModels.add(model);}

    public void setTrainedModels(){trainedModels = new LinkedList<Model>();}

    public String getDepartment() {return department;}

    public LinkedList<Model> getTrainedModels() {return trainedModels;}

    public String getStudentDegree () {return status.toString();}

    public Model getNextModel () {
        if(modelSent < models.length){
            Model model = models[modelSent];
            modelSent++;
            return model;
        }
        return null;
    }

    public Model[] getModels () {return models;}

    public int getModelSent() {return modelSent;}

    public void incrementPublications () {publications++;}

    public void incrementPapersRead () {papersRead++;}

    public int getPublications () {return publications;}

    public int getPapersRead () {return papersRead;}

}
