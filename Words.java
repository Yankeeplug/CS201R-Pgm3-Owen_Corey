//This class includes both the original, postive & negative review words
//This does not need to be done this way - it is just a suggestion
public class Words {
    String origWord;
    String editWord;
    String charWord;
    String posWord;
    String negWord;
    double sentOrigValue;
    double sentPosValue;
    double sentNegValue;
    static boolean posFlag = false;
    static boolean negFlag = false;

    public Words(String o, String e, String p, String n, double s, double sp, double sn){
        origWord = o;
        editWord = e; 
        posWord = p;
        negWord = n;
        sentOrigValue = s;
        sentPosValue = sp;
        sentNegValue = sn;
    }
    public static void setPosFlag(boolean s){
        posFlag = s;
    }
    public static void setNegFlag(boolean s){
        negFlag = s;
    }

    public static boolean getPosFlag(){
        return posFlag;
    }
    public static boolean getNegFlag(){
        return negFlag;
    }

    public String getOrigWord() { return origWord; }
    public String getEditWord() { return editWord; }
    public String getPosWord() { return posWord; }
    public String getNegWord() { return negWord; }
}
