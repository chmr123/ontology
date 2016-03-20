/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package traceability.groupVerbs.cm1;

/**
 *
 * @author Zeheng Li
 */
public class XMLpairs {
    public XMLmodel a;
    public XMLmodel b;
    
    public XMLpairs(XMLmodel a, XMLmodel b){
        this.a=a;
        this.b=b;
    }
    
    public boolean equals (Object x){
        XMLpairs nx=(XMLpairs)x;
        if (nx.a.equals(a) && nx.b.equals(b) || nx.b.equals(a) && nx.a.equals(b))
            return true;
        return false;
    }
}
