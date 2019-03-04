package testvladkuz.buhsoftttm.classes;

public class Items {

    int id;
    String docid, title, alccode, capacity, volume, shortname, far1, far2, alcmark, factnums, code, type;
    int nums;
    public Items(){
    }

    public Items(String docid, String title){
        this.docid = docid;
        this.title = title;
    }

    public Items(int id, String docid, String title, String alccode, String capacity, String volume, String shortname, String far1, String far2, String alcmark, int nums, String factnums, String code, String type){
        this.id = id;
        this.docid = docid;
        this.title = title;
        this.alccode = alccode;
        this.capacity = capacity;
        this.volume = volume;
        this.shortname = shortname;
        this.far1 = far1;
        this.far2 = far2;
        this.alcmark = alcmark;
        this.nums = nums;
        this.factnums = factnums;
        this.code = code;
        this.type = type;
    }

    public Items(String docid, String title, String alccode, String capacity, String volume, String shortname, String far1, String far2, String alcmark, int nums, String factnums, String code, String type){
        this.docid = docid;
        this.title = title;
        this.alccode = alccode;
        this.capacity = capacity;
        this.volume = volume;
        this.shortname = shortname;
        this.far1 = far1;
        this.far2 = far2;
        this.alcmark = alcmark;
        this.nums = nums;
        this.factnums = factnums;
        this.code = code;
        this.type = type;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getDocid(){
        return this.docid;
    }

    public void setDocid(String docid){
        this.docid = docid;
    }

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getAlccode(){
        return this.alccode;
    }

    public void setAlccode(String alccode){
        this.alccode = alccode;
    }

    public String getCapacity(){
        return this.capacity;
    }

    public void setCapacity(String capacity){
        this.capacity = capacity;
    }

    public String getVolume(){
        return this.volume;
    }

    public void setVolume(String volume){
        this.volume = volume;
    }

    public String getShortname(){
        return this.shortname;
    }

    public void setShortname(String shortname){
        this.shortname = shortname;
    }

    public String getFar1(){
        return this.far1;
    }

    public void setFar1(String far1){
        this.far1 = far1;
    }

    public String getFar2(){
        return this.far2;
    }

    public void setFar2(String far2){
        this.far2 = far2;
    }

    public String getAlcmark(){
        return this.alcmark;
    }

    public void setAlcmark(String alcmark){
        this.alcmark = alcmark;
    }

    public int getNums(){
        return this.nums;
    }

    public void setNums(int nums){
        this.nums = nums;
    }

    public String getFactnums(){
        return this.factnums;
    }

    public void setFactnums(String factnums){
        this.factnums = factnums;
    }

    public String getCode(){
        return this.code;
    }

    public void setCode(String code){
        this.code = code;
    }

    public String getType(){
        return this.type;
    }

    public void setType(String type){
        this.type = type;
    }

}

