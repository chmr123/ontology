/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package traceability.groupVerbs.cm1;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Zeheng Li
 */
public class XMLreader {
    
    private File folderh,folderl;
    private File[] listh;
    private File[] listl;
    private ArrayList<XMLmodel> hlist,llist;
    private ArrayList<String> dependencyTypes;
    public XMLreader (String fh, String fl){
        
        folderh = new File(fh);
        folderl = new File(fl);
        listh = folderh.listFiles();
        listl = folderl.listFiles();
        hlist = new ArrayList();
        llist = new ArrayList();
        dependencyTypes = new ArrayList<String>();
        dependencyTypes.add("dobj");
        dependencyTypes.add("iobj");
        
    }
    
    public ArrayList<XMLmodel> getHighDependency(int i){
        
        //open folder and read all the dependencies into the arraylist
        try{
            
            int sum1=0;
            int sum2=0;
            
           // for (int i=0;i<listh.length;i++){
                
                
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(listh[i]);
                NodeList nl=doc.getElementsByTagName("collapsed-ccprocessed-dependencies");
               // System.out.println("doc# "+(i+1)+" has "+nl.getLength()+" sentence(s) in the document");
                                
                for (int j=0;j<nl.getLength();j++){
                    Element dependencies = (Element) nl.item(j);
                    NodeList ds = dependencies.getElementsByTagName("dep");
                   // System.out.println("    each contains "+ds.getLength()+" dependencies");                   
                    
                    for(int k=0;k<ds.getLength();k++){
                        Element dependency = (Element) ds.item(k);
                        String t=dependency.getAttributeNode("type").getNodeValue();
                        if(!dependencyTypes.contains(t)) continue;
                        String g=dependency.getElementsByTagName("governor").item(0).getFirstChild().getNodeValue();
                        String d=dependency.getElementsByTagName("dependent").item(0).getFirstChild().getNodeValue();
                        //if(d.matches("[^A-Za-z]")) continue;
                        XMLmodel m = new XMLmodel(t,g,d);
                        if(!hlist.contains(m)){
                            hlist.add(m);
                            sum1++;
                        }
                        
                    }                    
                }
                
          //  }
            
            
            /*System.out.println(sum1+"|"+sum2);
            for (int i=0;i<hlist.size();i++){
                System.out.println(hlist.get(i).type+": ("+hlist.get(i).governor+","+hlist.get(i).dependent+")");
            }
            
            for (int i=0;i<llist.size(); i++){
                System.out.println(llist.get(i).type+": ("+llist.get(i).governor+","+llist.get(i).dependent+")");
            }*/
            
            //nonpairs();
            
            //pairs();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return hlist;
        
    }
    
    public ArrayList<XMLmodel> getLowDependency(int i){
        
        //open folder and read all the dependencies into the arraylist
        try{
            
         
        	//for (int i=0;i<listl.length;i++){
                              
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(listl[i]);
                NodeList nl=doc.getElementsByTagName("collapsed-ccprocessed-dependencies");
              //  System.out.println("doc# "+(i+1)+" has "+nl.getLength()+" sentence(s) in the document");

                for (int j=0;j<nl.getLength();j++){
                    Element dependencies = (Element) nl.item(j);
                    NodeList ds = dependencies.getElementsByTagName("dep");
                   // System.out.println("    each contains "+ds.getLength()+" dependencies");
                    
                    for(int k=0;k<ds.getLength();k++){
                        Element dependency = (Element) ds.item(k);
                        String t=dependency.getAttributeNode("type").getNodeValue();
                        if(!dependencyTypes.contains(t)) continue;
                        String g=dependency.getElementsByTagName("governor").item(0).getFirstChild().getNodeValue();
                        String d=dependency.getElementsByTagName("dependent").item(0).getFirstChild().getNodeValue();
                       // if(d.matches("[^A-Za-z]")) continue;
                        XMLmodel m = new XMLmodel(t,g,d);
                        if(!llist.contains(m)){
                            llist.add(m);
                        }
                        
                    }                    
                }
                
           // }
            
    
            /*System.out.println(sum1+"|"+sum2);
            for (int i=0;i<hlist.size();i++){
                System.out.println(hlist.get(i).type+": ("+hlist.get(i).governor+","+hlist.get(i).dependent+")");
            }
            
            for (int i=0;i<llist.size(); i++){
                System.out.println(llist.get(i).type+": ("+llist.get(i).governor+","+llist.get(i).dependent+")");
            }*/
            
            //nonpairs();
            
           // pairs();
        }
        catch (Exception e){
            e.printStackTrace();
        }
		return llist;
        
    }
    
    private void nonpairs(){
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("C:/Users/Zeheng Li/Desktop/parsefeatures")));
            
            ArrayList<XMLmodel> dlist = new ArrayList();
            for(int i=0;i<hlist.size();i++){
                dlist.add(hlist.get(i));
            }
            
            for(int i=0;i<llist.size();i++){
                if(!dlist.contains(llist.get(i))){
                    dlist.add(llist.get(i));
                }
            }
            
            for(int i=0;i<listh.length;i++){            
                for(int j=0;j<listl.length;j++){
                    
                    ArrayList<XMLmodel> instance = new ArrayList();
                    
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(listh[i]);
                    NodeList nl=doc.getElementsByTagName("collapsed-ccprocessed-dependencies");
                    for (int k=0;k<nl.getLength();k++){                   
                        Element dependencies = (Element) nl.item(k);
                        NodeList ds = dependencies.getElementsByTagName("dep");                    
                        for(int l=0;l<ds.getLength();l++){
                            Element dependency = (Element) ds.item(l);
                            String t=dependency.getAttributeNode("type").getNodeValue();
                            if(!(t.equals("nsubj")||t.equals("dobj"))) continue;
                            String g=dependency.getElementsByTagName("governor").item(0).getFirstChild().getNodeValue();
                            String d=dependency.getElementsByTagName("dependent").item(0).getFirstChild().getNodeValue();
                            XMLmodel m = new XMLmodel(t,g,d);
                            if(!instance.contains(m)){
                                instance.add(m);
                            }                       
                        }                    
                    }
                    
                    DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder2 = factory2.newDocumentBuilder();
                    Document doc2 = builder2.parse(listl[j]);
                    NodeList nl2=doc2.getElementsByTagName("collapsed-ccprocessed-dependencies");                   
                    for (int k=0;k<nl2.getLength();k++){                   
                        Element dependencies = (Element) nl2.item(k);
                        NodeList ds = dependencies.getElementsByTagName("dep");                    
                        for(int l=0;l<ds.getLength();l++){
                            Element dependency = (Element) ds.item(l);
                            String t=dependency.getAttributeNode("type").getNodeValue();
                            if(!(t.equals("nsubj")||t.equals("dobj"))) continue;
                            String g=dependency.getElementsByTagName("governor").item(0).getFirstChild().getNodeValue();
                            String d=dependency.getElementsByTagName("dependent").item(0).getFirstChild().getNodeValue();
                            XMLmodel m = new XMLmodel(t,g,d);
                            if(!instance.contains(m)){
                                instance.add(m);
                            }                       
                        }                    
                    }
                    
                    //write matrix
                    //System.out.println(instance.size());
                    
                    String row="";
                    if(instance.size()==0){
                        row="-1";
                    }
                    else{
                        for (int k=0;k<instance.size();k++){
                            row+=(dlist.indexOf(instance.get(k))+1)+",";
                        }
                        row=row.substring(0,row.length()-1);
                        
                    }
                    bw.write(row);
                    bw.newLine();
                }            
            }
            bw.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }    
    
    private void pairs(){
        
        ArrayList<XMLpairs> wp = new ArrayList();
        for(int i=0;i<hlist.size();i++){            
                for(int j=0;j<llist.size();j++){
                    
                    XMLpairs np = new XMLpairs(hlist.get(i),llist.get(j));
                    
                    if(!wp.contains(np))
                        wp.add(np);
                    
                    
                    
                }
        }
        try{
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("C:/Users/Zeheng Li/Desktop/parsepairsfeatures")));
        
        for (int i=0;i<listh.length;i++){
            for (int j=0;j<listl.length;j++){
            
                
                    
                    
                    
                    ArrayList<XMLmodel> hh = new ArrayList();
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(listh[i]);
                    NodeList nl=doc.getElementsByTagName("collapsed-ccprocessed-dependencies");
                    for (int k=0;k<nl.getLength();k++){                   
                        Element dependencies = (Element) nl.item(k);
                        NodeList ds = dependencies.getElementsByTagName("dep");                    
                        for(int l=0;l<ds.getLength();l++){
                            Element dependency = (Element) ds.item(l);
                            String t=dependency.getAttributeNode("type").getNodeValue();
                            if(!(t.equals("nsubj")||t.equals("dobj"))) continue;
                            String g=dependency.getElementsByTagName("governor").item(0).getFirstChild().getNodeValue();
                            String d=dependency.getElementsByTagName("dependent").item(0).getFirstChild().getNodeValue();
                            XMLmodel m = new XMLmodel(t,g,d);
                            if(!hh.contains(m)){
                                hh.add(m);
                            }                       
                        }                    
                    }
                    
                    ArrayList<XMLmodel> ll = new ArrayList();
                    DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder2 = factory2.newDocumentBuilder();
                    Document doc2 = builder2.parse(listl[j]);
                    NodeList nl2=doc2.getElementsByTagName("collapsed-ccprocessed-dependencies");                   
                    for (int k=0;k<nl2.getLength();k++){                   
                        Element dependencies = (Element) nl2.item(k);
                        NodeList ds = dependencies.getElementsByTagName("dep");                    
                        for(int l=0;l<ds.getLength();l++){
                            Element dependency = (Element) ds.item(l);
                            String t=dependency.getAttributeNode("type").getNodeValue();
                            if(!(t.equals("nsubj")||t.equals("dobj"))) continue;
                            String g=dependency.getElementsByTagName("governor").item(0).getFirstChild().getNodeValue();
                            String d=dependency.getElementsByTagName("dependent").item(0).getFirstChild().getNodeValue();
                            XMLmodel m = new XMLmodel(t,g,d);
                            if(!ll.contains(m)){
                                ll.add(m);
                            }                       
                        }                    
                    }
                    ArrayList<Integer> index = new ArrayList();
                    for(int m=0;m<hh.size();m++)
                        for(int n=0;n<ll.size();n++){
                            
                            XMLpairs np = new XMLpairs(hh.get(m),ll.get(n));
                            
                            if(!index.contains(wp.indexOf(np))){
                                index.add(wp.indexOf(np));
                            }
                            
                        }
                    if(index.size()==0){
                        bw.write("-1");
                        bw.newLine();
                    }
                    else{
                        String str="";
                        for (int a=0;a<index.size();a++){
                            str+=(index.get(a)+1)+",";
                        }
                        str=str.substring(0,str.length()-1);
                        System.out.println(str);
                        bw.write(str);
                        bw.newLine();
                    }
                
                
            }
        }
        bw.close();
        }
                catch (Exception e){
                    e.printStackTrace();
                }
        
        System.out.println(wp.size());
        
    }
}
