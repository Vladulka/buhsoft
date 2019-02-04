package testvladkuz.buhsoftttm.sqldatabase;

import java.util.ArrayList;

import testvladkuz.buhsoftttm.classes.ALC;
import testvladkuz.buhsoftttm.classes.Items;
import testvladkuz.buhsoftttm.classes.TTM;

public interface IDatabaseHandler {

    public void addNewTTN(TTM ttm);
    public void deleteTTN(String id);
    public ArrayList<TTM> getAllTTM();
    public int getTTNSize();

    public void addNewToFooter(Items items);
    public void deleteFooter(String id);
    public ArrayList<Items> getAllItems(String id);
    public void updateItemStatus(String id);
    public int getItemsSize();


    public void addNewALC(ALC alc);
    public void deleteALC(String id);
    public String findALC(String alc, String id);
    public String findALCByMatrix(String alc, String id);
    public String findALCByPDF417(String alc, String id, boolean type);
    public String findALCByShtrih(String alc, String id);
    public void updateAlcStatus(String alc);
}
