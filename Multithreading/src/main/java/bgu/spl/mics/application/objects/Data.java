package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;
    private int size;

    public Data (String type, int size){
        this.size = size;
        processed = 0;
        if(type.equals("Images") || type.equals("images"))
            this.type = Type.Images;
        else if (type.equals("Text") || type.equals("text"))
            this.type = Type.Text;
        else
            this.type = Type.Tabular;
    }

    public int getSize(){
        return size;
    }

    public Type getType(){
        return type;
    }

    public int getTypeToInt () {
        if( type == Type.Images)
            return 4;
        if(type == Type.Text)
            return 2;
        return 1;
    }
}
