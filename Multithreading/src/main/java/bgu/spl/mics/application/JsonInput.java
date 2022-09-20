package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Student;

public class JsonInput {
    private Student[] Students;
    private String[] GPUS;
    private int[] CPUS;
    private ConfrenceInformation[] Conferences;
    private long TickTime;
    private long Duration;


    public JsonInput (Student[] _students, String[] _gpus, int[] _cpus, ConfrenceInformation[] _confrenceInformations, long _TickTime, Long _Duration) {
        this.Students = _students;
        this.GPUS = _gpus;
        this.CPUS = _cpus;
        this.Conferences = _confrenceInformations;
        this.TickTime = _TickTime;
        this.Duration = _Duration;
    }


    public Student[] getStudents(){return Students;}

    public String[] getGpus(){return GPUS;}

    public int[] getCpus(){return  CPUS;}

    public ConfrenceInformation[] getConfrenceInformations() {return Conferences;}

    public long getTickTime(){return TickTime;}

    public long getDuration() {return Duration;}

}
