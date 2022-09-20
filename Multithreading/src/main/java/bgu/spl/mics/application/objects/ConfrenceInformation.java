package bgu.spl.mics.application.objects;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    public LinkedBlockingQueue<Model> modelsToPublish;

    public ConfrenceInformation (String name, int date){
        this.name = name;
        this.date = date;
        this.modelsToPublish = new LinkedBlockingQueue<>();
    }

    public String getName() {return name;}

    public int getDate () {return date;}

    public void setModelsToPublish() {this.modelsToPublish = new LinkedBlockingQueue<>();}

    public LinkedBlockingQueue<Model> getModelsToPublish() {return modelsToPublish;}
}
