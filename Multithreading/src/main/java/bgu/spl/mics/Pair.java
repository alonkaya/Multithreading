package bgu.spl.mics;

public class Pair <First,Second> {
    private First FirstObject;
    private Second SecondObject;

    public Pair (First first, Second second) {
        this.FirstObject = first;
        this.SecondObject = second;
    }

    public First getFirstObject() {
        return FirstObject;
    }

    public Second getSecondObject() {
        return SecondObject;
    }

    public void setFirstObject(First other){FirstObject = other;}
}
