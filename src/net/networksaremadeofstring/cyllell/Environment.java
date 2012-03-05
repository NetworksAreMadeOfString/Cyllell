package net.networksaremadeofstring.cyllell;

public class Environment 
{
	//Basic Details
	private String Name;
	private String URI;
	private String Version;
	private Boolean SpinnerVisible = false;
	private Boolean Error = false;
	
	public Environment(String _Name, String _URI) 
    {
            super();
            this.Name = _Name;
            this.URI = _URI;
    }
	
	public Environment(String _Name, String _URI, String _Version) 
    {
            super();
            this.Name = _Name;
            this.URI = _URI;
            this.Version = _Version;
    }
	
	public String GetName()
	{
		return this.Name;
	}
	
	public String GetURI()
	{
		return this.URI;
	}
	
	public String GetVersion()
	{
		return this.Version;
	}
	
	public void SetErrorState()
	{
		this.Error = true;
	}
	
	public Boolean GetErrorState()
	{
		return this.Error;
	}
	
	public void SetSpinnerVisible()
	{
		this.SpinnerVisible = true;
	}
	
	public Boolean isSpinnerVisible()
	{
		return this.SpinnerVisible;
	}
}
