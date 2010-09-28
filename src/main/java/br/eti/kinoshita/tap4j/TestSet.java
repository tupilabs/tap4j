/*
 * The MIT License
 *
 * Copyright (c) <2010> <Bruno P. Kinoshita>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package br.eti.kinoshita.tap4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A Test Set is the top element in a TAP File. It holds references to the 
 * Header, Plan, List of Test Results and the rest of elements in TAP spec.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class TestSet 
implements Serializable
{

	/**
	 * TAP Header.
	 * @see {@link Header}
	 */
	private Header header;
	
	/**
	 * TAP Plan.
	 * @see {@link Plan}
	 */
	private Plan plan;
	
	/**
	 * List of TAP Lines.
	 * @see {@link TapLine]
	 * @see {@link TestResult}
	 * @see {@link BailOut}
	 */
	private List<TapLine> tapLines;
	
	/**
	 * TAP Footer.
	 * @see {@link Footer}
	 */
	private Footer footer;
	
	/**
	 * List of comments.
	 * @see {@link Comment}
	 */
	private List<Comment> comments;
	
	/**
	 * Default constructor.
	 */
	public TestSet()
	{
		super();
		this.tapLines = new ArrayList<TapLine>();
		this.comments = new ArrayList<Comment>();
		
	}
	
	/**
	 * @return TAP Header.
	 */
	public Header getHeader()
	{
		return this.header;
	}
	
	/**
	 * @param header TAP Header.
	 */
	public void setHeader( Header header )
	{
		this.header = header;
	}
	
	/**
	 * @return TAP Plan.
	 */
	public Plan getPlan()
	{
		return this.plan;
	}
	
	/**
	 * @param plan TAP Plan.
	 */
	public void setPlan( Plan plan )
	{
		this.plan = plan;
	}
	
	/**
	 * @return List of TAP Lines. These lines may be either a TestResult 
	 * or a BailOut.
	 * @see {@link TestResult}
	 * @see {@link BailOut}
	 */
	public List<TapLine> getTapLines()
	{
		return this.tapLines;
	}
	
	/**
	 * Adds a new TAP Line.
	 * 
	 * @param tapLine TAP Line.
	 * @return True if the TAP Line could be added into the list successfully.
	 */
	public boolean addTapLine( TapLine tapLine )
	{
		return this.tapLines.add( tapLine );
	}
	
	/**
	 * @param index Index of the TAP Line in the list to be removed.
	 * @return The removed TAP Line object.
	 */
	public TapLine removeTapLine( int index )
	{
		return this.tapLines.remove( index );
	}
	
	/**
	 * Removes a TAP Line from the list.
	 * 
	 * @param tapLine TAP Line object.
	 * @return True if could successfully remove the TAP Line from the list.
	 */
	public boolean removeTapLine( TapLine tapLine )
	{
		return this.tapLines.remove( tapLine );
	}
	
	/**
	 * @return Number of TAP Lines.
	 */
	public int countTapLines()
	{
		return this.tapLines.size();
	}
	
	/**
	 * @return Footer
	 */
	public Footer getFooter()
	{
		return this.footer;
	}
	
	/**
	 * @param footer Footer
	 */
	public void setFooter( Footer footer )
	{
		this.footer = footer;
	}
	
	/**
	 * @return List of Comments.
	 */
	public List<Comment> getComments()
	{
		return this.comments;
	}
	
	/**
	 * Adds a comment.
	 * 
	 * @param comment Comment.
	 * @return True if the Comment could be added into the list successfully.
	 */
	public boolean addComment( Comment comment )
	{
		return this.comments.add( comment );
	}
	
	/**
	 * @param index The index of the comment to be removed from the list.
	 * @return The removed Comment object.
	 */
	public Comment removeComment ( int index )
	{
		return this.comments.remove( index );
	}
	
	/**
	 * @param comment Comment.
	 * @return True if could successfully remove the Comment from the list.
	 */
	public boolean removeComment ( Comment comment )
	{
		return this.comments.remove( comment );
	}
	
	/**
	 * @return Number of comments in the List.
	 */
	public int countComments()
	{
		return this.comments.size();
	}
	
	/**
	 * @return True if it has any Bail Out statement, false otherwise.
	 */
	public boolean hasBailOut()
	{
		boolean isBailOut = false;
		
		for ( TapLine tapLine : tapLines )
		{
			if ( tapLine instanceof BailOut )
			{
				isBailOut = true;
				break;
			}
		}
		
		return isBailOut;
	}
	
}
