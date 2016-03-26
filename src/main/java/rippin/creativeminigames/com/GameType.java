package rippin.creativeminigames.com;

public enum GameType {
    TNTRUN("TNTRUN"), SNOWBALL("SNOWBALL");
    private String name;

    GameType(String s){
        this.name = s;
    }
    public String getString(){
        return name;
    }

}
