import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class word_analyzer1{
    private ArrayList<String> K = new ArrayList<String>(Arrays.asList("do","for","end", "if", "printf", "scanf", "then", "while", "switch", "case", "int", "double", "char"
    ,"break", "case", "else", "long", "short", "float", "const"));
    private ArrayList<String> edge = new ArrayList<String>(Arrays.asList(";", ",", "(", ")", "[", "]"));
    private ArrayList<String> As = new ArrayList<String>(Arrays.asList("+","-","*","/"));
    private ArrayList<String> Rs = new ArrayList<String>(Arrays.asList("=","<",">"));
    private ArrayList<String> ci = new ArrayList<String>();
    private ArrayList<String> variables = new ArrayList<String>();
    private ArrayList<outtoken> outtokens = new ArrayList<outtoken>();
    private int i;
    private int j;

    public void set(){
        i = j = 0;
    }
    public boolean isedge(char s){
        String ss = String.valueOf(s);
        for(String sign : edge){
            if(ss.equals(sign)){
                return true;
            }
        }
        return false;
    }

    public boolean is_basic_Lr(char s){
        String ss = String.valueOf(s);
        for(String sign :Rs){
            if(sign.equals(ss)){
                return true;
            }
        }
        return false;
    }
    public boolean is_basic_as(char s){
        String ss = String.valueOf(s);
        for(String sign :As){
            if(sign.equals(ss)){
                return true;
            }
        }
        return false;
    }
    public void process_word(String txt, int row, int column){
        int variable = 1;
        int k = i+1;
        for( ;k<txt.length();k++){
            if(!Character.isLetter(txt.charAt(k)) && !Character.isDigit(txt.charAt(k))&&txt.charAt(k)!='_'){
                break;
            }
        }
        j = k;
        String w = txt.substring(i, k);
        for(String key : K){
            if(w.equals(key)){
                variable = 0;
                outtoken ot = new outtoken(key, 1, 0, row, column);
                outtokens.add(ot);
                break;
            }
        }
        if(variable==1){
            boolean require_add = true;
            if (!variables.isEmpty()){
                for(String v :variables){
                    if(v.equals(w)){
                        require_add = false;
                        outtoken ot = new outtoken(w, 6, 0, row, column);
                        outtokens.add(ot);
                    }
                }
            }
            if(require_add){
                variables.add(w);
                outtoken ot = new outtoken(w, 6, 0, row, column);
                outtokens.add(ot);
            }
        }
    }
    public void process_num(String txt, int row, int column){
        int error = 0;
        int isc = 1;
        int k = i+1;
        int pointcount = 0;
        for( ;k<txt.length();k++ ){
            if(Character.isLetter(txt.charAt(k))){
                isc = 0;
            }
            if(txt.charAt(k)=='.'){
                pointcount++;
            }
            if(!Character.isDigit(txt.charAt(k)) && !Character.isLetter(txt.charAt(k)) && txt.charAt(k)!='.'){
                break;
            }
        }
        error = pointcount >1?1:0;
        j = k;
        String w = txt.substring(i, k);
        if(isc==1){
            outtoken ot = new outtoken(w, 5, error, row, column);
            outtokens.add(ot);
            ci.add(w);
        }
        else{
            outtoken ot = new outtoken(w, 0, error, row, column);
            outtokens.add(ot);
        }

    }
    public void process_as(String txt, char temp, int row, int column){
        int error = 0;
        j++;
        for(;j<txt.length();j++){
            if(is_basic_as(txt.charAt(j))&&temp == txt.charAt(j)){
                error = 1;
            }
            else{
                break;
            }
        }
        outtoken ot = new outtoken(txt.substring(i, j), 4, error, row, column);
        outtokens.add(ot);

    }
    public void process_edge(String txt, int row, int column){
        j++;
        outtoken ot = new outtoken(txt.substring(i, j), 2, 0, row, column);
        outtokens.add(ot);
    }
    public void process_ls(String txt, char temp, int row, int column){
        int error = 0;//从基本关系运算符中寻找
        j++;
        for(;j<txt.length();j++){
            if(is_basic_Lr(txt.charAt(j))&&temp == txt.charAt(j)){
                error = 1;
            }
            else{
                break;
            }
        }
        outtoken ot = new outtoken(txt.substring(i, j), 4, error, row, column);
        outtokens.add(ot);
    }

    public void process(String txt, int row){
        int column = 0;
        for(; i < txt.length();){
            char temp = txt.charAt(i);
            if(j<txt.length()){
                boolean flag = true;
                if(temp == ' '||temp == '\n'||temp == '\t') {
                    i++;
                    j++;
                    continue;
                }
                column++;
                if(Character.isLetter(temp)){
                    flag = false;
                    process_word(txt, row, column);
                }
                //处理常数
                else if(Character.isDigit(temp)){
                    flag = false;
                    process_num(txt, row, column);
                }
                else if(is_basic_as(temp)){
                    flag = false;
                    process_as(txt, temp, row, column);
                }
                else if(isedge(temp)){
                    flag = false;
                    process_edge(txt, row, column);
                }
                else if(is_basic_Lr(temp)){
                    flag = false;
                    process_ls(txt, temp, row, column);
                }

                if(flag){
                    j++;
                    outtoken ot = new outtoken(txt.substring(i, j), 4, 1, row, column);
                    outtokens.add(ot);
                }
                i = j;
            }
            else{
                break;
            }
        }
    }
    public void showtoken(){
        for(outtoken token : outtokens){
            token.show_token();
        }
    }
    public static void main(String [] args) throws IOException {
        int row = 0;
        word_analyzer1 wa = new word_analyzer1();
        Scanner in  = new Scanner(Path.of("C:\\Users\\10672\\IntelliJIDEAProjects\\compile_test_exp1\\src\\data1.txt"), StandardCharsets.UTF_8);
        System.out.println("单词"+"\t\t\t\t"+"二元序列"+"\t\t\t\t"+"类型"+"\t\t\t\t"+"位置（行，列）");
        while(in.hasNextLine()){
            row++;
            wa.set();
            String line = in.nextLine();
            wa.process(line, row);
        }
        wa.showtoken();
    }


}
