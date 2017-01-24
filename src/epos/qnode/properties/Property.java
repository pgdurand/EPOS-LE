package epos.qnode.properties;

import java.io.Serializable;

import org.jdesktop.application.AbstractBean;

/**
 * A property object is a simple key value pair stored in the backend module. After storage,
 * the Property gets its unique id. The id and the name of the property have to be the same
 * if two properties should be equal.
 *<p>
 *Even though this is an entity class, no method does actually open a transaction. This has
 *to be done by the user, outside of this class. 
 *<p>
 *Typically {@link Property}s are stored in a {@link PropertySet}. The set manages different 
 *properties in one context (name) and provide storage methods. If we store Properties in {@link PropertySet}s,
 *we have to make sure that links are removed properly. If you delete the Property, you want the link
 *in the {@link PropertySet} removed as well. To ensure that, we use the {@link PreRemove} annotated method
 *that checks {@link PropertySet} for this property and disconnects them. 
 * 
 * 
 * @author Thasso Griebel
 *
 */
public class Property extends AbstractBean {
	/**
	 * The Properties id
	 */
	protected long id;
	/**
	 * The properties name
	 */
	protected String name;
	/**
	 * The property value		
	 */
	private Serializable value;
	
	/**
	 * Internal constructor
	 */
	protected Property(){		
	}	
	/**
	 * Create a new property with given name.
	 * 
	 * @param name property name
	 */
	public Property(String name){
		this();
		setName(name);
	}
	/**
	 * Create a new property with given name and value
	 * 
	 * @param name property name
	 * @param value property value
	 */
	public Property(String name, Serializable value) {
		this(name);
		setValue(value);
	}
	/**
	 * @return name of the property
	 */
	public String getName() {
		return name;
	}
	/**
	 * Set the name of this property
	 * @param name of the property
	 */
	protected void setName(String name) {
		String old = this.name;
		this.name = name;
		firePropertyChange("name", old, this.name);
	}

	/**
	 * Returns the value of this property
	 * @return value of this proeprty
	 */
	public Serializable getValue() {
		return value;
	}
	/**
	 * Sets the value of this property
	 * @param value of this property
	 */
	public void setValue(Serializable value) {
		Object old = this.value;
		this.value = value;
		firePropertyChange(getName(), old, this.value);
		firePropertyChange("value", old, this.value);
	}
	/**
	 * @return id of this property
	 */
	public long getId() {
		return id;
	}
	/**
	 * Set the id of this property
	 * @param id of this property
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * hash code corresponds to the id
	 */
	public int hashCode(){
		return name.hashCode();
	}
	/**
	 * Two properties are equal if they have the same id and the same name 
	 */
	public boolean equals(Object o){
		if(o instanceof Property){
			Property other = (Property) o;
			return this.getId() == other.getId() && getName().equals(other.getName());
		}
		return false;
	}	
}
