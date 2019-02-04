package testvladkuz.buhsoftttm.classes;

public class TTM {

    int id;
    private String title, fsrar, guid, date, inn, shortname, status;

    public TTM(){}

    public TTM(int id, String title, String fsrar, String guid, String date, String inn, String shortname, String status){
        this.id = id;
        this.title = title;
        this.fsrar = fsrar;
        this.guid = guid;
        this.date = date;
        this.inn = inn;
        this.shortname = shortname;
        this.status = status;
    }

    public TTM(String title, String fsrar, String guid, String date, String inn, String shortname, String status){
        this.title = title;
        this.fsrar = fsrar;
        this.guid = guid;
        this.date = date;
        this.inn = inn;
        this.shortname = shortname;
        this.status = status;
    }

    ///////////////////////////////////////////////

    public int getId(){
        return this.id;
    }

    public String getTitle(){
        return this.title;
    }

    public String getFsrar(){
        return this.fsrar;
    }

    public String getGuid(){
        return this.guid;
    }

    public String getDate(){
        return this.date;
    }

    public String getInn(){
        return this.inn;
    }

    public String getShortname(){
        return this.shortname;
    }

    public String getStatus(){
        return this.status;
    }

    ///////////////////////////////////////////////

    public void setId(int id){
        this.id = id;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setFsrar(String fsrar){
        this.fsrar = fsrar;
    }

    public void setGuid(String guid){
        this.guid = guid;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setInn(String inn){
        this.inn = inn;
    }

    public void setShortname(String shortname){
        this.shortname = shortname;
    }

    public void setStatus(String status){
        this.status = status;
    }


}

