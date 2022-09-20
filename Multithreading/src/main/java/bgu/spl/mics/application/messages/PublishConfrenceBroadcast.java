package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;

import java.util.Vector;

public class PublishConfrenceBroadcast implements Broadcast {
    private ConfrenceInformation published;

    public PublishConfrenceBroadcast (ConfrenceInformation confrence) {
        this.published = confrence;
    }

    public ConfrenceInformation getPublished() {return published;}
}
