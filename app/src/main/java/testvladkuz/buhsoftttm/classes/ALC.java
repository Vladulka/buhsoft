package testvladkuz.buhsoftttm.classes;

public class ALC {

    private String docid, itemid, alc, status;

    public ALC(){}

    public ALC(String docid, String itemid, String alc, String status){
        this.docid = docid;
        this.itemid = itemid;
        this.alc = alc;
        this.status = status;
    }

    ///////////////////////////////////////////////

    public String getDocid(){
        return this.docid;
    }

    public String getItemid(){
        return this.itemid;
    }

    public String getAlc(){
        return this.alc;
    }

    public String getStatus(){
        return this.status;
    }

    ///////////////////////////////////////////////

    public void setDocid(String docid){
        this.docid = docid;
    }

    public void setItemid(String itemid){
        this.itemid = itemid;
    }

    public void setAlc(String alc){
        this.alc = alc;
    }

    public void setStatus(String status){
        this.status = status;
    }

}

