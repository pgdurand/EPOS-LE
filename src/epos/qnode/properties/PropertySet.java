package epos.qnode.properties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdesktop.application.AbstractBean;
/**
 * A {@link PropertySet} is a named collections of Properties. The property set is a DB 
 * Entity and can be persistenly stored.<p>
 * The set stores {@link Property}s and can be persisted, but is not stored automatically.
 * If you want to store a set and all its properties, use teh {@link #merge()} method.
 * 
 * @author Thasso Griebel
 *
 */
public class PropertySet extends AbstractBean implements Serializable, Iterable<Property>{
	/**
	 * the sets id
	 */
	private long id = -1;
	/**
	 * The sets name
	 */
	protected String name;
	/**
	 * Properties stored in this set
	 */
	private List<Property> properties;
	/**
	 * Create a new property set
	 */
	public PropertySet(){		
	}	
	/**
	 * Create a new PropertySet with a given name
	 * 
	 * @param name
	 */
	public PropertySet(String name){
		super();
		setName(name);
	}
	/**
	 * @return name of this set
	 */
	public String getName() {
		return name;
	}
	/**
	 * Sets the name of this set
	 * @param name new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return id if this set
	 */
	public long getId() {
		return id;
	}
	/**
	 * Sets the id of this set
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}		
	/**
	 * hashcode based on the id
	 */
	public int hashCode(){
		return (int) getId();
	}	
	/**
	 * Two sets are equal if they have the same id
	 */
	public boolean equals(Object o){
		if(o == null) return false;
		if(o == this) return true;
		if(!(o instanceof PropertySet)) return false;
		PropertySet p = (PropertySet) o;
		return p.getId() == ((PropertySet)o).getId();
	}
	
	/**
	 * Return the list of properties associated with this set. 
	 * <p>
	 * This is a protected method and should not be used from outside.
	 * The class encapsulates access to the collection.
	 * 
	 * @return list of properties associated with this set
	 */
	protected List<Property> getProperties() {
		if(properties == null){
			properties = new ArrayList<Property>();
		}
		return properties;
	}
	/**
	 * Set the properties associated with this set.
	 * 
	 * @param properties {@link Property}s assiciated with this set
	 */
	protected void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	
	/**
	 * Returns a property with given name or null if no such property exists
	 * 
	 * @param name of the property
	 * @return property with given name or null
	 */
	public Property getProperty(String name){
		for (Property p : getProperties()) {
			if(p.getName().equals(name))return p;
		}
		return null;
	}
	
	/**
	 * Sets a property to the given value. A null name is not permitted. If the property 
	 * exists, the values is set, otherwise the property gets created.
	 *  
	 * @param name of the property
	 * @param value of the property 
	 * @return true if successful
	 */
	public boolean set(String name, Serializable value){
		if(name == null) return false;
		
		Property p = getProperty(name);
		if(p == null){
			p = new Property(name);
		}		
		p.setValue(value);
		return set(p);		
	}
	
	/**
	 * Adds the given property to the set of properties if and only if
	 * there is no other property with the same name.
	 * 
	 * @param property to be added
	 * @return true if added
	 */
	public boolean set(Property p){
		if(p == null) return false;
		boolean nameMatch = false;
		for (Property  other : getProperties()) {
			if(other.getName().equals(p.getName())){
				nameMatch = true;
				break;
			}
		}
		
		if(!nameMatch){
			return properties.add(p);
		}
		// already contained
		return true;
	}
	/**
	 * Returns the value of of the property with the given name or null if no such property exists.
	 * 
	 * @param name of the property
	 * @return value of the property or null
	 */
	public Serializable get(String name){
		return get(name, null);
	}
	/**
	 * Returns the value of the property with the given name or the given default value if
	 * no such property exists.
	 * 
	 * @param name of the property 
	 * @param defaultValue of the property
	 * @return value of the property or the given default value
	 */
	public Serializable get(String name, Serializable defaultValue){
		Property p = getProperty(name);
		if(p != null){
			return p.getValue();
		}
		return defaultValue;
	}
	/**
	 * Adds the given property to the set of properties.
	 *  
	 * @param p property to be set
	 * @return true if successful
	 */
	public boolean add(Property p){
		return set(p);
	}
	/**
	 * Sets the value of the property with the given name. If no such property exists,
	 * it gets created.
	 * 
	 * @param name of the property
	 * @param value of the property
	 * @return true if successful
	 * @see #set(String, Serializable)
	 */
	public boolean  add(String name, Serializable value){
		return set(name, value);
	}
	/**
	 * Returns the number of properties in this property set
	 * 
	 * @return number of properties
	 */
	public int size(){
		return getProperties().size();
	}
	/**
	 * Removes the given property from the set of properties
	 * 
	 * @param property to be removed
	 * @return true if successful
	 */
	public boolean remove(Property property) {
		if(property == null) return false;
		return getProperties().remove(property);
	}
	/**
	 * Removes the property with the given name if such property exists.
	 * 
	 * @param name of the property 
	 * @return true if successful
	 * @see #remove(Property)
	 */
	public boolean remove(String name){
		Property p = null;
		for (Property pr : getProperties()) {
			if(pr.getName().equals(name)){
				p = pr;
				break;
			}
		}
		return remove(p);
	}
	/**
	 * Returns true if this set contains the given property
	 * 
	 * @param property to search for
	 * @return true if this set contains the given property
	 */
	public boolean contains(Property property) {
		return getProperties().contains(property);		
	}
	/**
	 * @param name of the property
	 * @return true if this set contains a property with the given name
	 */
	public boolean contains(String name){	
		for (Property pr : getProperties()) {
			if(pr.getName().equals(name)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Iterate over the set of properties
	 */
	public Iterator<Property> iterator() {
		return getProperties().iterator();
	}
	
}