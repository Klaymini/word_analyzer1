public class outtoken {
    private static String[] typelist = new String[]{"关键字", "分界符", "算术运算符", "关系运算符", "无符号数", "标识符"};
    private int type;
    private int error = 0;
    private String word;
    private int x;
    private int y;

    outtoken(String word, int type, int error, int row, int column){
        this.word = word;
        this.type = type;
        this.error = error;
        x = row;
        y = column;
    }

    public void show_token(){
        if (error==0){
            System.out.println(word+"\t\t\t\t"+'('+type+','+word+')'+"\t\t\t\t"+typelist[type-1]+"\t\t\t\t"+'('+x+','+y+')');
        }
        else{
            System.out.println(word+"\t\t\t\t"+"Error"+"\t\t\t\t"+"Error"+"\t\t\t\t"+'('+x+','+y+')');
        }

    }
}
