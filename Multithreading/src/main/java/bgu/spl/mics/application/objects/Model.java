package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    enum Status {PreTrained, Training, Trained, Tested};
    enum Result {None, Good, Bad};
    private String name;
    private Data data;
    private String type;
    private int size;
    private Student student;
    private Status status;
    private Result result;
    private boolean published;



    public Model(String name, String type, int size){
        this.name = name;
        this.data = new Data(type,size);
        this.type = type;
        this.size = size;
        this.status = Status.PreTrained;
        this.result = Result.None;
        published = false;
    }

    public void setData(){
        this.data = new Data(type,size);
        this.status = Status.PreTrained;
        this.result = Result.None;}

    public void setStudent(Student s) {this.student = s;}

    public void setPublished(boolean set){this.published = set;}

    public boolean isPublished() {return published;}

    public String getName(){return name;}

    public Data getData() {return data;}

    public Student getStudent() {return student;}

    public Status getStatus(){ return  status;}

    public String getCurrResult() {return result.toString();}


    public void setStatus(String other){
        if(other.equals("Training"))
            status = Status.Training;
        else if (other.equals("PreTrained"))
            status = Status.PreTrained;
        else if (other.equals("Trained"))
            status = Status.Trained;
        else
            status = Status.Tested;
    }


    public void setResult(String other){
        if(other.equals("Good"))
            result = Result.Good;
        else
            result = Result.Bad;
    }
}
