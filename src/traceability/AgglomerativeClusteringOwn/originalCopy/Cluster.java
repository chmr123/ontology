package traceability.AgglomerativeClusteringOwn.originalCopy;

import java.util.ArrayList;

public class Cluster {
	private ArrayList<String> members;
	int clusterID;
	
	public Cluster(){
		members = new ArrayList<String>();
	}
	
	
	public void addMember(String member){
		this.members.add(member);
	}
	
	
	public ArrayList<String> getMembers(){
		return members;
	}
	
	public void setClusterID(int id){
		this.clusterID = id;
	}
	
	public int getClusterID(){
		return this.clusterID;
	}
	
	@Override
	public String toString(){
		String members = "";
		for(String member : this.members){
			members = members + member + ",";
		}
		return members.substring(0,members.lastIndexOf(","));
	}
}
