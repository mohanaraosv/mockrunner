package com.mockrunner.tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mockrunner.base.HTMLOutputModule;
import com.mockrunner.base.NestedApplicationException;
import com.mockrunner.base.VerifyFailedException;
import com.mockrunner.mock.web.MockJspWriter;
import com.mockrunner.mock.web.MockPageContext;
import com.mockrunner.mock.web.WebMockObjectFactory;
import com.mockrunner.util.common.StringUtil;

/**
 * Module for custom tag tests. Simulates the container by
 * performing the tag lifecycle.
 */
public class TagTestModule extends HTMLOutputModule
{
    private final static Log log = LogFactory.getLog(TagTestModule.class);
    private WebMockObjectFactory mockFactory;
    private TagSupport tag;
    private boolean caseSensitive;

    public TagTestModule(WebMockObjectFactory mockFactory)
    {
        super(mockFactory);
        this.mockFactory = mockFactory;
        caseSensitive = true;
    }
    
    /**
     * Set if {@link #verifyOutput} and {@link #verifyOutputContains}
     * should compare case sensitive. Default is <code>true</code>.
     * @param caseSensitive enable or disable case sensitivity
     */
    public void setCaseSensitive(boolean caseSensitive)
    {
        this.caseSensitive = caseSensitive;
    }
    
    /**
     * Creates a tag. Internally a {@link NestedTag}
     * is created but the wrapped tag is returned. If you
     * simply want to test the output of the tag without 
     * nesting other tags, you do not have to care about the
     * {@link NestedTag}, just use the returned instance.
     * An empty attribute <code>Map</code> will be used for
     * the tag.
     * @param tagClass the class of the tag
     * @return instance of <code>TagSupport</code> or <code>BodyTagSupport</code>
     */
    public TagSupport createTag(Class tagClass)
    {
        return createTag(tagClass, new HashMap());
    }
    
    /**
     * Creates a tag. Internally a {@link NestedTag}
     * is created but the wrapped tag is returned. If you
     * simply want to test the output of the tag without 
     * nesting other tags, you do not have to care about the
     * {@link NestedTag}, just use the returned instance.
     * The attributes <code>Map</code> contains the attributes
     * of this tag (<i>propertyname</i> maps to <i>propertyvalue</i>).
     * The attributes are populated (i.e. the tags setters are called)
     * during the lifecycle or with an explicit call of
     * {@link #populateAttributes}.
     * @param tagClass the class of the tag
     * @param attributes the attribute map
     * @return instance of <code>TagSupport</code> or <code>BodyTagSupport</code>
     */
    public TagSupport createTag(Class tagClass, Map attributes)
    {
        return createNestedTag(tagClass, attributes).getTag();
    }
    
    /**
     * Creates a {@link NestedTag} and returns it. You can
     * add child tags or body blocks to the {@link NestedTag}.
     * Use {@link #getTag} to get the wrapped tag.
     * An empty attribute <code>Map</code> will be used for
     * the tag.
     * @param tagClass the class of the tag
     * @return instance of {@link NestedStandardTag} or {@link NestedBodyTag}
     */
    public NestedTag createNestedTag(Class tagClass)
    {
        return createNestedTag(tagClass, new HashMap());
    }
    
    /**
     * Creates a {@link NestedTag} and returns it. You can
     * add child tags or body blocks to the {@link NestedTag}.
     * Use {@link #getTag} to get the wrapped tag.
     * The attributes <code>Map</code> contains the attributes
     * of this tag (<i>propertyname</i> maps to <i>propertyvalue</i>).
     * The attributes are populated (i.e. the tags setters are called)
     * during the lifecycle or with an explicit call of
     * {@link #populateAttributes}.
     * @param tagClass the class of the tag
     * @param attributes the attribute map
     * @return instance of {@link NestedStandardTag} or {@link NestedBodyTag}
     */
    public NestedTag createNestedTag(Class tagClass, Map attributes)
    {
        try
        {
            this.tag = (TagSupport)TagUtil.createNestedTagInstance(tagClass, getMockPageContext(), attributes);
            return (NestedTag)this.tag;
        }
        catch(Exception exc)
        {
            log.error(exc.getMessage(), exc);
            throw new NestedApplicationException(exc);
        }
    }
    
    /**
     * Creates a {@link NestedTag} and returns it. You can
     * add child tags or body blocks to the {@link NestedTag}.
     * Use {@link #getTag} to get the wrapped tag.
     * An empty attribute <code>Map</code> will be used for
     * the tag.
     * @param tag the tag
     * @return instance of {@link NestedStandardTag} or {@link NestedBodyTag}
     */
    public NestedTag setTag(TagSupport tag)
    {
        return setTag(tag, new HashMap());
    }
    
    /**
     * Creates a {@link NestedTag} and returns it. You can
     * add child tags or body blocks to the {@link NestedTag}.
     * Use {@link #getTag} to get the wrapped tag.
     * The attributes <code>Map</code> contains the attributes
     * of this tag (<i>propertyname</i> maps to <i>propertyvalue</i>).
     * The attributes are populated (i.e. the tags setters are called)
     * during the lifecycle or with an explicit call of
     * {@link #populateAttributes}.
     * @param tag the tag
     * @param attributes the attribute map
     * @return instance of {@link NestedStandardTag} or {@link NestedBodyTag}
     */
    public NestedTag setTag(TagSupport tag, Map attributes)
    {
        try
        {
            this.tag = (TagSupport)TagUtil.createNestedTagInstance(tag, getMockPageContext(), attributes);
            return (NestedTag)this.tag;
        }
        catch(Exception exc)
        {
            log.error(exc.getMessage(), exc);
            throw new RuntimeException(exc.getMessage());
        }
    }
    
    /**
     * Specify if the <code>release</code> method should be called
     * before populating a tag. Delegates to {@link NestedTag#setDoRelease}
     * Defaults to <code>false</code>. It's the container behaviour to call 
     * <code>release</code>, but it's usually not necessary in the tests, 
     * because the tag instances are not reused during a test run.
     * @param doRelease should release be called
     */
    public void setDoRelease(boolean doRelease)
    {
        if(null == tag)
        {
            throw new RuntimeException("Not current tag set");
        }
        ((NestedTag)tag).setDoRelease(doRelease);
    }
    
    /**
     * Specify if the <code>release</code> method should be called
     * before populating a tag. Delegates to {@link NestedTag#setDoReleaseRecursive}
     * Defaults to <code>false</code>. It's the container behaviour to call 
     * <code>release</code>, but it's usually not necessary in the tests, 
     * because the tag instances are not reused during a test run.
     * @param doRelease should release be called
     */
    public void setDoReleaseRecursive(boolean doRelease)
    {
        if(null == tag)
        {
            throw new RuntimeException("Not current tag set");
        }
        ((NestedTag)tag).setDoReleaseRecursive(doRelease);
    }
    
    /**
     * Populates the attributes of the underlying tag by
     * calling {@link NestedTag#populateAttributes}. The setters
     * of the tag are called. Please note that child tags are not
     * populated. This is done during the lifecycle.
     */
    public void populateAttributes()
    {
        if(null == tag)
        {
            throw new RuntimeException("Not current tag set");
        }
        ((NestedTag)tag).populateAttributes();
    }
    
    /**
     * Sets the body of the tag as a static string. Please
     * note that all childs of the underlying {@link NestedTag}
     * are deleted and the static content is set. If you want
     * to use nested tags, please use the method {@link NestedTag#addTextChild}
     * to set static content.
     * @param body the static body content
     */
    public void setBody(String body)
    {
        if(null == tag)
        {
            throw new RuntimeException("Not current tag set");
        }
        ((NestedTag)tag).removeChilds();
        ((NestedTag)tag).addTextChild(body);
    }
    
    /**
     * Returns the current wrapped tag.
     * @return instance of <code>TagSupport</code> or <code>BodyTagSupport</code>
     */
    public TagSupport getTag()
    {
        if(null == tag) return null;
        return ((NestedTag)tag).getTag();
    }
    
    /**
     * Returns the current nested tag. You can
     * add child tags or body blocks to the {@link NestedTag}.
     * Use {@link #getTag} to get the wrapped tag.
     * @return instance of {@link NestedStandardTag} or {@link NestedBodyTag}
     */
    public NestedTag getNestedTag()
    {
        return (NestedTag)tag;
    }
    
    /**
     * Returns the <code>MockPageContext</code> object.
     * Delegates to {@link com.mockrunner.mock.web.WebMockObjectFactory#getMockPageContext}.
     * @return the MockPageContext
     */
    public MockPageContext getMockPageContext()
    {
        return mockFactory.getMockPageContext();
    }
    
    /**
     * Calls the <code>doStartTag</code> method of the current tag.
     * @return the result of <code>doStartTag</code>
     */
    public int doStartTag()
    {
        if(null == tag)
        {
            throw new RuntimeException("No current tag set");
        }
        try
        {
            return tag.doStartTag();
        }
        catch(JspException exc)
        {
            log.error(exc.getMessage(), exc);
            throw new RuntimeException(exc.getMessage());
        }
    }
    
    /**
     * Calls the <code>doEndTag</code> method of the current tag.
     * @return the result of <code>doEndTag</code>
     */
    public int doEndTag()
    {
        if(null == tag)
        {
            throw new RuntimeException("No current tag set");
        }
        try
        {
            return tag.doEndTag();
        }
        catch(JspException exc)
        {
            log.error(exc.getMessage(), exc);
            throw new RuntimeException(exc.getMessage());
        }
    }
    
    /**
     * Calls the <code>doInitBody</code> method of the current tag.
     * @throws RuntimeException if the current tag is no body tag
     */
    public void doInitBody()
    {
        if(null == tag)
        {
            throw new RuntimeException("No current tag set");
        }
        if(!isBodyTag()) 
        {
            throw new RuntimeException("current tag is no body tag");
        }
        try
        {
            NestedBodyTag bodyTag = (NestedBodyTag)tag;
            bodyTag.doInitBody();
        }
        catch(JspException exc)
        {
            log.error(exc.getMessage(), exc);
            throw new RuntimeException(exc.getMessage());
        }
    }

    /**
     * Calls the <code>doAfterBody</code> method of the current tag.
     * @return the result of <code>doAfterBody</code>
     */
    public int doAfterBody()
    {
        if(null == tag)
        {
            throw new RuntimeException("No current tag set");
        }
        try
        {
            return tag.doAfterBody();
        }
        catch(JspException exc)
        {
            log.error(exc.getMessage(), exc);
            throw new RuntimeException(exc.getMessage());
        }
    }

    /**
     * Calls the <code>release</code> method of the current tag.
     */
    public void release()
    {
        tag.release();
    }
    
    /**
     * Performs the tags lifecycle by calling {@link NestedTag#doLifecycle}.
     * All <code>doBody</code> and <code>doTag</code> methods are called as 
     * in the real web container. The evaluation of the body is simulated 
     * by performing the lifecycle recursively for all childs of the 
     * {@link NestedTag}.
     * @return the result of the final <code>doEndTag</code> call
     */
    public int processTagLifecycle()
    {
        if(null == tag)
        {
            throw new RuntimeException("No current tag set");
        }
        try
        {
            return ((NestedTag)tag).doLifecycle();
        }
        catch(JspException exc)
        {
            log.error(exc.getMessage(), exc);
            throw new RuntimeException(exc.getMessage());
        }
    }
    
    /**
     * Resets the output buffer.
     */
    public void clearOutput()
    {
        MockJspWriter writer = (MockJspWriter)mockFactory.getMockPageContext().getOut();
        try
        {
            writer.clearBuffer();
        }
        catch(IOException exc)
        {
            log.error(exc.getMessage(), exc);
            throw new RuntimeException(exc.getMessage());
        }
    }
    
    /**
     * Gets the output data the current tag has rendered. Makes only sense
     * after calling at least {@link #doStartTag} or {@link #processTagLifecycle}
     * @return the output data
     */
    public String getOutput()
    {
        MockJspWriter writer = (MockJspWriter)mockFactory.getMockPageContext().getOut();
        return writer.getOutputAsString();
    }

    /**
     * Verifies the tag output.
     * @param expectedOutput the expected output.
     * @throws VerifyFailedException if verification fails
     */
    public void verifyOutput(String expectedOutput)
    {
        String actualOutput = getOutput();
        if(!StringUtil.matchesExact(actualOutput, expectedOutput, caseSensitive))
        {
            throw new VerifyFailedException("actual output: " + actualOutput + " does not match expected output");
        }
    }
    
    /**
     * Verifies if the tag output contains the specified data.
     * @param expectedOutput the data
     * @throws VerifyFailedException if verification fails
     */
    public void verifyOutputContains(String expectedOutput)
    {
        String actualOutput = getOutput();
        if(!StringUtil.matchesContains(actualOutput, expectedOutput, caseSensitive))
        {
            throw new VerifyFailedException("actual output: " + actualOutput + " does not match expected output");
        }
    }
    
    /**
     * Verifies if the tag output matches the specified
     * regular expression.
     * @param expression the data
     * @throws VerifyFailedException if verification fails
     */
    public void verifyOutputRegularExpression(String expression)
    {
        String actualOutput = getOutput();
        if(!StringUtil.matchesPerl5(actualOutput, expression, caseSensitive))
        {
            throw new VerifyFailedException("actual output: " + actualOutput + " does not match expected output");
        }
    }
    
    private boolean isBodyTag()
    {
        return (tag instanceof NestedBodyTag);
    }
}
