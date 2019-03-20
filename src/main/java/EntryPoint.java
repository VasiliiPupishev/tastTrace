import javax.swing.text.Document;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;

class Node{
    public String Number = "";
    public String ip = "";
    public String as = "";
    Node(String i, String j, String u){
        Number = i;
        ip = j;
        as = u;
    }
    Node(){}

    @Override
    public String toString() {
        return Number + " " + ip + " " + as;
    }
}

public class EntryPoint {
    public static void main(String[] arg){
        int count = 0;
        Scanner in = new Scanner(System.in);
        System.out.println("Input ip or url");
        String name = in.nextLine();
        StringBuilder cmdRes = new StringBuilder();
        ArrayList<Node> r = new ArrayList<Node>();
        String s = null;
        try {
            Process p = Runtime.getRuntime().exec("tracert " + name);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                count++;
                if(count < 5){
                    continue;
                }
                cmdRes.append(s).append("\n");
                String[] part = s.split(" ");
                ArrayList<String> parts = new ArrayList<String>();
                for (String i: part) {
                    if(i.length() != 0){
                        parts.add(i);
                    }
                }
                Node newNode = new Node();
                int c = 0;
                String res = "";
                int num = 0;
                Boolean cont = false;
                for(int i = 0; i < parts.size(); i++) {
                    if (parts.get(i).equals("*")) {
                        cont = true;
                    }
                    if(c == 0 && newNode.Number.length() == 0){
                        newNode.Number = parts.get(i);
                        r.add(newNode);
                        c++;
                    }
                    if(i == parts.size() - 1){
                        String g = parts.get(i);
                        g = g.replaceAll("]", "");
                        g = g.replaceAll("\\[", "");
                        newNode.ip = g;
                    }
                }
                if (cont){
                    continue;
                }
                System.out.print(newNode.toString() + "   ");
                if (newNode.ip.length() > 4){
                    try {
                        URL url = new URL("http://ipinfo.io/" + newNode.ip + "/json");

                        try {
                            LineNumberReader reader = new LineNumberReader(new InputStreamReader(url.openStream()));
                            String string = reader.readLine();
                            while (string != null) {
                                System.out.print(getInfo(string));
                                string = reader.readLine();
                            }
                            reader.close();
                        } catch (IOException e) {
                            //e.printStackTrace();
                        }

                    } catch (MalformedURLException ex) {
                        //ex.printStackTrace();
                    }
                }
                System.out.println();
            }

            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }
        //String[] lines = cmdRes.toString().split("\n");
        //for (int i = 0; i < r.size(); i++){
            //System.out.println(r.get(i).toString());
        //}
        //System.out.println(cmdRes);
        System.exit(0);
    }

    private static String getInfo(String string) {
        String res = "";
        String[] parts = string.split("\n");
        for (String str: parts) {
            String[] part = str.split(" ");
            if (part.length > 2) {
                if (part[2].equals("\"city\":")) {
                    if (part[3].length() > 3) {
                        res += "city " + part[3] + " ";
                    }
                }
                if (part[2].equals("\"region\":")) {
                    if (part[3].length() > 3) {
                        res += "region " + part[3] + " ";
                    }
                }
                if (part[2].equals("\"country\":")) {
                    if (part[3].length() > 3) {
                        res += "country " + part[3] + " ";
                    }
                }
                if (part[2].equals("\"org\":")) {
                    if (part[3].length() > 3) {
                        res += "org " + part[3] + " ";
                    }
                }
            }
        }
        return res;
    }
}