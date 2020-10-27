/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package ai.platon.pulsar.persist.gora.generated;  

public class GActiveDomStat extends org.apache.gora.persistency.impl.PersistentBase implements org.apache.avro.specific.SpecificRecord, org.apache.gora.persistency.Persistent {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"GActiveDomStat\",\"namespace\":\"ai.platon.pulsar.persist.gora.generated\",\"fields\":[{\"name\":\"ni\",\"type\":\"int\",\"default\":0},{\"name\":\"na\",\"type\":\"int\",\"default\":0},{\"name\":\"nnm\",\"type\":\"int\",\"default\":0},{\"name\":\"nst\",\"type\":\"int\",\"default\":0},{\"name\":\"w\",\"type\":\"int\",\"default\":0},{\"name\":\"h\",\"type\":\"int\",\"default\":0}]}");
  private static final long serialVersionUID = 7139016527831633653L;
  /** Enum containing all data bean's fields. */
  public static enum Field {
    NI(0, "ni"),
    NA(1, "na"),
    NNM(2, "nnm"),
    NST(3, "nst"),
    W(4, "w"),
    H(5, "h"),
    ;
    /**
     * Field's index.
     */
    private int index;

    /**
     * Field's name.
     */
    private String name;

    /**
     * Field's constructor
     * @param index field's index.
     * @param name field's name.
     */
    Field(int index, String name) {this.index=index;this.name=name;}

    /**
     * Gets field's index.
     * @return int field's index.
     */
    public int getIndex() {return index;}

    /**
     * Gets field's name.
     * @return String field's name.
     */
    public String getName() {return name;}

    /**
     * Gets field's attributes to string.
     * @return String field's attributes to string.
     */
    public String toString() {return name;}
  };

  public static final String[] _ALL_FIELDS = {
  "ni",
  "na",
  "nnm",
  "nst",
  "w",
  "h",
  };

  /**
   * Gets the total field count.
   * @return int field count
   */
  public int getFieldsCount() {
    return GActiveDomStat._ALL_FIELDS.length;
  }

  private int ni;
  private int na;
  private int nnm;
  private int nst;
  private int w;
  private int h;
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return ni;
    case 1: return na;
    case 2: return nnm;
    case 3: return nst;
    case 4: return w;
    case 5: return h;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value) {
    switch (field$) {
    case 0: ni = (java.lang.Integer)(value); break;
    case 1: na = (java.lang.Integer)(value); break;
    case 2: nnm = (java.lang.Integer)(value); break;
    case 3: nst = (java.lang.Integer)(value); break;
    case 4: w = (java.lang.Integer)(value); break;
    case 5: h = (java.lang.Integer)(value); break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'ni' field.
   */
  public java.lang.Integer getNi() {
    return ni;
  }

  /**
   * Sets the value of the 'ni' field.
   * @param value the value to set.
   */
  public void setNi(java.lang.Integer value) {
    this.ni = value;
    setDirty(0);
  }
  
  /**
   * Checks the dirty status of the 'ni' field. A field is dirty if it represents a change that has not yet been written to the database.
   * @param value the value to set.
   */
  public boolean isNiDirty() {
    return isDirty(0);
  }

  /**
   * Gets the value of the 'na' field.
   */
  public java.lang.Integer getNa() {
    return na;
  }

  /**
   * Sets the value of the 'na' field.
   * @param value the value to set.
   */
  public void setNa(java.lang.Integer value) {
    this.na = value;
    setDirty(1);
  }
  
  /**
   * Checks the dirty status of the 'na' field. A field is dirty if it represents a change that has not yet been written to the database.
   * @param value the value to set.
   */
  public boolean isNaDirty() {
    return isDirty(1);
  }

  /**
   * Gets the value of the 'nnm' field.
   */
  public java.lang.Integer getNnm() {
    return nnm;
  }

  /**
   * Sets the value of the 'nnm' field.
   * @param value the value to set.
   */
  public void setNnm(java.lang.Integer value) {
    this.nnm = value;
    setDirty(2);
  }
  
  /**
   * Checks the dirty status of the 'nnm' field. A field is dirty if it represents a change that has not yet been written to the database.
   * @param value the value to set.
   */
  public boolean isNnmDirty() {
    return isDirty(2);
  }

  /**
   * Gets the value of the 'nst' field.
   */
  public java.lang.Integer getNst() {
    return nst;
  }

  /**
   * Sets the value of the 'nst' field.
   * @param value the value to set.
   */
  public void setNst(java.lang.Integer value) {
    this.nst = value;
    setDirty(3);
  }
  
  /**
   * Checks the dirty status of the 'nst' field. A field is dirty if it represents a change that has not yet been written to the database.
   * @param value the value to set.
   */
  public boolean isNstDirty() {
    return isDirty(3);
  }

  /**
   * Gets the value of the 'w' field.
   */
  public java.lang.Integer getW() {
    return w;
  }

  /**
   * Sets the value of the 'w' field.
   * @param value the value to set.
   */
  public void setW(java.lang.Integer value) {
    this.w = value;
    setDirty(4);
  }
  
  /**
   * Checks the dirty status of the 'w' field. A field is dirty if it represents a change that has not yet been written to the database.
   * @param value the value to set.
   */
  public boolean isWDirty() {
    return isDirty(4);
  }

  /**
   * Gets the value of the 'h' field.
   */
  public java.lang.Integer getH() {
    return h;
  }

  /**
   * Sets the value of the 'h' field.
   * @param value the value to set.
   */
  public void setH(java.lang.Integer value) {
    this.h = value;
    setDirty(5);
  }
  
  /**
   * Checks the dirty status of the 'h' field. A field is dirty if it represents a change that has not yet been written to the database.
   * @param value the value to set.
   */
  public boolean isHDirty() {
    return isDirty(5);
  }

  /** Creates a new GActiveDomStat RecordBuilder */
  public static ai.platon.pulsar.persist.gora.generated.GActiveDomStat.Builder newBuilder() {
    return new ai.platon.pulsar.persist.gora.generated.GActiveDomStat.Builder();
  }
  
  /** Creates a new GActiveDomStat RecordBuilder by copying an existing Builder */
  public static ai.platon.pulsar.persist.gora.generated.GActiveDomStat.Builder newBuilder(ai.platon.pulsar.persist.gora.generated.GActiveDomStat.Builder other) {
    return new ai.platon.pulsar.persist.gora.generated.GActiveDomStat.Builder(other);
  }
  
  /** Creates a new GActiveDomStat RecordBuilder by copying an existing GActiveDomStat instance */
  public static ai.platon.pulsar.persist.gora.generated.GActiveDomStat.Builder newBuilder(ai.platon.pulsar.persist.gora.generated.GActiveDomStat other) {
    return new ai.platon.pulsar.persist.gora.generated.GActiveDomStat.Builder(other);
  }
  
  private static java.nio.ByteBuffer deepCopyToReadOnlyBuffer(
      java.nio.ByteBuffer input) {
    java.nio.ByteBuffer copy = java.nio.ByteBuffer.allocate(input.capacity());
    int position = input.position();
    input.reset();
    int mark = input.position();
    int limit = input.limit();
    input.rewind();
    input.limit(input.capacity());
    copy.put(input);
    input.rewind();
    copy.rewind();
    input.position(mark);
    input.mark();
    copy.position(mark);
    copy.mark();
    input.position(position);
    copy.position(position);
    input.limit(limit);
    copy.limit(limit);
    return copy.asReadOnlyBuffer();
  }
  
  /**
   * RecordBuilder for GActiveDomStat instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<GActiveDomStat>
    implements org.apache.avro.data.RecordBuilder<GActiveDomStat> {

    private int ni;
    private int na;
    private int nnm;
    private int nst;
    private int w;
    private int h;

    /** Creates a new Builder */
    private Builder() {
      super(ai.platon.pulsar.persist.gora.generated.GActiveDomStat.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(ai.platon.pulsar.persist.gora.generated.GActiveDomStat.Builder other) {
      super(other);
    }
    
    /** Creates a Builder by copying an existing GActiveDomStat instance */
    private Builder(ai.platon.pulsar.persist.gora.generated.GActiveDomStat other) {
            super(ai.platon.pulsar.persist.gora.generated.GActiveDomStat.SCHEMA$);
      if (isValidValue(fields()[0], other.ni)) {
        this.ni = (java.lang.Integer) data().deepCopy(fields()[0].schema(), other.ni);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.na)) {
        this.na = (java.lang.Integer) data().deepCopy(fields()[1].schema(), other.na);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.nnm)) {
        this.nnm = (java.lang.Integer) data().deepCopy(fields()[2].schema(), other.nnm);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.nst)) {
        this.nst = (java.lang.Integer) data().deepCopy(fields()[3].schema(), other.nst);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.w)) {
        this.w = (java.lang.Integer) data().deepCopy(fields()[4].schema(), other.w);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.h)) {
        this.h = (java.lang.Integer) data().deepCopy(fields()[5].schema(), other.h);
        fieldSetFlags()[5] = true;
      }
    }

    /** Gets the value of the 'ni' field */
    public java.lang.Integer getNi() {
      return ni;
    }
    
    /** Sets the value of the 'ni' field */
    public ai.platon.pulsar.persist.gora.generated.GActiveDomStat.Builder setNi(int value) {
      validate(fields()[0], value);
      this.ni = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'ni' field has been set */
    public boolean hasNi() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'ni' field */
    public ai.platon.pulsar.persist.gora.generated.GActiveDomStat.Builder clearNi() {
      fieldSetFlags()[0] = false;
      return this;
    }
    
    /** Gets the value of the 'na' field */
    public java.lang.Integer getNa() {
      return na;
    }
    
    /** Sets the value of the 'na' field */
    public ai.platon.pulsar.persist.gora.generated.GActiveDomStat.Builder setNa(int value) {
      validate(fields()[1], value);
      this.na = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'na' field has been set */
    public boolean hasNa() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'na' field */
    public ai.platon.pulsar.persist.gora.generated.GActiveDomStat.Builder clearNa() {
      fieldSetFlags()[1] = false;
      return this;
    }
    
    /** Gets the value of the 'nnm' field */
    public java.lang.Integer getNnm() {
      return nnm;
    }
    
    /** Sets the value of the 'nnm' field */
    public ai.platon.pulsar.persist.gora.generated.GActiveDomStat.Builder setNnm(int value) {
      validate(fields()[2], value);
      this.nnm = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'nnm' field has been set */
    public boolean hasNnm() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'nnm' field */
    public ai.platon.pulsar.persist.gora.generated.GActiveDomStat.Builder clearNnm() {
      fieldSetFlags()[2] = false;
      return this;
    }
    
    /** Gets the value of the 'nst' field */
    public java.lang.Integer getNst() {
      return nst;
    }
    
    /** Sets the value of the 'nst' field */
    public ai.platon.pulsar.persist.gora.generated.GActiveDomStat.Builder setNst(int value) {
      validate(fields()[3], value);
      this.nst = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'nst' field has been set */
    public boolean hasNst() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'nst' field */
    public ai.platon.pulsar.persist.gora.generated.GActiveDomStat.Builder clearNst() {
      fieldSetFlags()[3] = false;
      return this;
    }
    
    /** Gets the value of the 'w' field */
    public java.lang.Integer getW() {
      return w;
    }
    
    /** Sets the value of the 'w' field */
    public ai.platon.pulsar.persist.gora.generated.GActiveDomStat.Builder setW(int value) {
      validate(fields()[4], value);
      this.w = value;
      fieldSetFlags()[4] = true;
      return this; 
    }
    
    /** Checks whether the 'w' field has been set */
    public boolean hasW() {
      return fieldSetFlags()[4];
    }
    
    /** Clears the value of the 'w' field */
    public ai.platon.pulsar.persist.gora.generated.GActiveDomStat.Builder clearW() {
      fieldSetFlags()[4] = false;
      return this;
    }
    
    /** Gets the value of the 'h' field */
    public java.lang.Integer getH() {
      return h;
    }
    
    /** Sets the value of the 'h' field */
    public ai.platon.pulsar.persist.gora.generated.GActiveDomStat.Builder setH(int value) {
      validate(fields()[5], value);
      this.h = value;
      fieldSetFlags()[5] = true;
      return this; 
    }
    
    /** Checks whether the 'h' field has been set */
    public boolean hasH() {
      return fieldSetFlags()[5];
    }
    
    /** Clears the value of the 'h' field */
    public ai.platon.pulsar.persist.gora.generated.GActiveDomStat.Builder clearH() {
      fieldSetFlags()[5] = false;
      return this;
    }
    
    @Override
    public GActiveDomStat build() {
      try {
        GActiveDomStat record = new GActiveDomStat();
        record.ni = fieldSetFlags()[0] ? this.ni : (java.lang.Integer) defaultValue(fields()[0]);
        record.na = fieldSetFlags()[1] ? this.na : (java.lang.Integer) defaultValue(fields()[1]);
        record.nnm = fieldSetFlags()[2] ? this.nnm : (java.lang.Integer) defaultValue(fields()[2]);
        record.nst = fieldSetFlags()[3] ? this.nst : (java.lang.Integer) defaultValue(fields()[3]);
        record.w = fieldSetFlags()[4] ? this.w : (java.lang.Integer) defaultValue(fields()[4]);
        record.h = fieldSetFlags()[5] ? this.h : (java.lang.Integer) defaultValue(fields()[5]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
  
  public GActiveDomStat.Tombstone getTombstone(){
  	return TOMBSTONE;
  }

  public GActiveDomStat newInstance(){
    return newBuilder().build();
  }

  private static final Tombstone TOMBSTONE = new Tombstone();
  
  public static final class Tombstone extends GActiveDomStat implements org.apache.gora.persistency.Tombstone {
  
      private Tombstone() { }
  
	  		  /**
	   * Gets the value of the 'ni' field.
		   */
	  public java.lang.Integer getNi() {
	    throw new java.lang.UnsupportedOperationException("Get is not supported on tombstones");
	  }
	
	  /**
	   * Sets the value of the 'ni' field.
		   * @param value the value to set.
	   */
	  public void setNi(java.lang.Integer value) {
	    throw new java.lang.UnsupportedOperationException("Set is not supported on tombstones");
	  }
	  
	  /**
	   * Checks the dirty status of the 'ni' field. A field is dirty if it represents a change that has not yet been written to the database.
		   * @param value the value to set.
	   */
	  public boolean isNiDirty() {
	    throw new java.lang.UnsupportedOperationException("IsDirty is not supported on tombstones");
	  }
	
				  /**
	   * Gets the value of the 'na' field.
		   */
	  public java.lang.Integer getNa() {
	    throw new java.lang.UnsupportedOperationException("Get is not supported on tombstones");
	  }
	
	  /**
	   * Sets the value of the 'na' field.
		   * @param value the value to set.
	   */
	  public void setNa(java.lang.Integer value) {
	    throw new java.lang.UnsupportedOperationException("Set is not supported on tombstones");
	  }
	  
	  /**
	   * Checks the dirty status of the 'na' field. A field is dirty if it represents a change that has not yet been written to the database.
		   * @param value the value to set.
	   */
	  public boolean isNaDirty() {
	    throw new java.lang.UnsupportedOperationException("IsDirty is not supported on tombstones");
	  }
	
				  /**
	   * Gets the value of the 'nnm' field.
		   */
	  public java.lang.Integer getNnm() {
	    throw new java.lang.UnsupportedOperationException("Get is not supported on tombstones");
	  }
	
	  /**
	   * Sets the value of the 'nnm' field.
		   * @param value the value to set.
	   */
	  public void setNnm(java.lang.Integer value) {
	    throw new java.lang.UnsupportedOperationException("Set is not supported on tombstones");
	  }
	  
	  /**
	   * Checks the dirty status of the 'nnm' field. A field is dirty if it represents a change that has not yet been written to the database.
		   * @param value the value to set.
	   */
	  public boolean isNnmDirty() {
	    throw new java.lang.UnsupportedOperationException("IsDirty is not supported on tombstones");
	  }
	
				  /**
	   * Gets the value of the 'nst' field.
		   */
	  public java.lang.Integer getNst() {
	    throw new java.lang.UnsupportedOperationException("Get is not supported on tombstones");
	  }
	
	  /**
	   * Sets the value of the 'nst' field.
		   * @param value the value to set.
	   */
	  public void setNst(java.lang.Integer value) {
	    throw new java.lang.UnsupportedOperationException("Set is not supported on tombstones");
	  }
	  
	  /**
	   * Checks the dirty status of the 'nst' field. A field is dirty if it represents a change that has not yet been written to the database.
		   * @param value the value to set.
	   */
	  public boolean isNstDirty() {
	    throw new java.lang.UnsupportedOperationException("IsDirty is not supported on tombstones");
	  }
	
				  /**
	   * Gets the value of the 'w' field.
		   */
	  public java.lang.Integer getW() {
	    throw new java.lang.UnsupportedOperationException("Get is not supported on tombstones");
	  }
	
	  /**
	   * Sets the value of the 'w' field.
		   * @param value the value to set.
	   */
	  public void setW(java.lang.Integer value) {
	    throw new java.lang.UnsupportedOperationException("Set is not supported on tombstones");
	  }
	  
	  /**
	   * Checks the dirty status of the 'w' field. A field is dirty if it represents a change that has not yet been written to the database.
		   * @param value the value to set.
	   */
	  public boolean isWDirty() {
	    throw new java.lang.UnsupportedOperationException("IsDirty is not supported on tombstones");
	  }
	
				  /**
	   * Gets the value of the 'h' field.
		   */
	  public java.lang.Integer getH() {
	    throw new java.lang.UnsupportedOperationException("Get is not supported on tombstones");
	  }
	
	  /**
	   * Sets the value of the 'h' field.
		   * @param value the value to set.
	   */
	  public void setH(java.lang.Integer value) {
	    throw new java.lang.UnsupportedOperationException("Set is not supported on tombstones");
	  }
	  
	  /**
	   * Checks the dirty status of the 'h' field. A field is dirty if it represents a change that has not yet been written to the database.
		   * @param value the value to set.
	   */
	  public boolean isHDirty() {
	    throw new java.lang.UnsupportedOperationException("IsDirty is not supported on tombstones");
	  }
	
		  
  }

  private static final org.apache.avro.io.DatumWriter
            DATUM_WRITER$ = new org.apache.avro.specific.SpecificDatumWriter(SCHEMA$);
  private static final org.apache.avro.io.DatumReader
            DATUM_READER$ = new org.apache.avro.specific.SpecificDatumReader(SCHEMA$);

  /**
   * Writes AVRO data bean to output stream in the form of AVRO Binary encoding format. This will transform
   * AVRO data bean from its Java object form to it s serializable form.
   *
   * @param out java.io.ObjectOutput output stream to write data bean in serializable form
   */
  @Override
  public void writeExternal(java.io.ObjectOutput out)
          throws java.io.IOException {
    out.write(super.getDirtyBytes().array());
    DATUM_WRITER$.write(this, org.apache.avro.io.EncoderFactory.get()
            .directBinaryEncoder((java.io.OutputStream) out,
                    null));
  }

  /**
   * Reads AVRO data bean from input stream in it s AVRO Binary encoding format to Java object format.
   * This will transform AVRO data bean from it s serializable form to deserialized Java object form.
   *
   * @param in java.io.ObjectOutput input stream to read data bean in serializable form
   */
  @Override
  public void readExternal(java.io.ObjectInput in)
          throws java.io.IOException {
    byte[] __g__dirty = new byte[getFieldsCount()];
    in.read(__g__dirty);
    super.setDirtyBytes(java.nio.ByteBuffer.wrap(__g__dirty));
    DATUM_READER$.read(this, org.apache.avro.io.DecoderFactory.get()
            .directBinaryDecoder((java.io.InputStream) in,
                    null));
  }
  
}

