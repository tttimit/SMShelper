package tttimit.com.smshelper.Domain;

/**
 * Created by tttimit on 2016/5/29.
 */
public class Item {
    public int id;
    public String name;
    public String number;
    public String time;

    public Item(){}

    public Item(int id, String name, String number, String time){
        this.id = id;
        this.name = name;
        this.number = number;
        this.time = time;
    }
}
