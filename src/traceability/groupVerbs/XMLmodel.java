/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package traceability.groupVerbs;

import java.util.Locale;


/**
 *
 * @author Zeheng Li
 */
public class XMLmodel {
    public String type;
    public String governor;
    public String dependent;
    
    public XMLmodel (String t, String g, String d){
        type=t.toLowerCase();
        governor=g.toLowerCase();
        dependent=d.toLowerCase();
    }
    
    public boolean equals(Object x){
        XMLmodel nx=(XMLmodel)x;
        if (nx.dependent.equals(dependent) && nx.governor.equals(governor) && nx.type.equals(type))
            return true;
        return false;
    }
    
}
