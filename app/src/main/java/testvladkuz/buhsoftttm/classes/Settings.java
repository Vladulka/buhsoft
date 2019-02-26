package testvladkuz.buhsoftttm.classes;

public class Settings {

    int _id;
    String _znach;
    String _text;

    public Settings(){
    }

    public Settings(int id, String _znach, String _text){
        this._id = id;
        this._znach = _znach;
        this._text = _text;
    }

    public Settings(String _znach, String _text){
        this._znach = _znach;
        this._text = _text;
    }

    public int getID(){
        return this._id;
    }

    public void setID(int id){
        this._id = id;
    }

    public String getZn(){
        return this._znach;
    }

    public void setZn(String _znach){
        this._znach = _znach;
    }

    public String getText(){
        return this._text;
    }

    public void setText(String _text){
        this._text = _text;
    }


}

