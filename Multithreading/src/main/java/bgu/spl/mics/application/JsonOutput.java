package bgu.spl.mics.application;
import bgu.spl.mics.application.objects.*;
import com.google.gson.annotations.Expose;

public class JsonOutput {
    @Expose
    private Student[] students;

    private ConfrenceInformation[] confrenceInformations;
    private int GpuTimeUsed;
    private int CpuTimeUsed;

    private int BatchesProcessed;


    public void setStudents(Student[] _students){
        this.students = _students;
    }

    public void setConfrenceInformations (ConfrenceInformation[] _confrenceInformations){
        this.confrenceInformations = _confrenceInformations;
    }

    public void setGpuTimeUsed (int GpuTime) {
        this.GpuTimeUsed = GpuTime;
    }

    public void setCpuTimeUsed (int CpuTime) {
        this.CpuTimeUsed = CpuTime;
    }

    public void setBatchesProcessed (int BatchesProcessed) {
        this.BatchesProcessed = BatchesProcessed;
    }


}