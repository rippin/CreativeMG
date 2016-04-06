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

    public static boolean contains(String s){
        for (GameType type : values()){
            System.out.println(type.getString());
            if (type.getString().equalsIgnoreCase(s))
                return true;
        }
        return false;
    }

    public static GameType getFromString(String s){
        for (GameType type : values()){
            if (type.getString().equalsIgnoreCase(s))
                return type;
        }
        return null;
    }

}
