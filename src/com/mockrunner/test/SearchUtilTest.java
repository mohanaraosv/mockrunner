package com.mockrunner.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mockrunner.util.SearchUtil;

import junit.framework.TestCase;

public class SearchUtilTest extends TestCase
{
    public void testGetMatchingObjects()
    {
        Map testMap = new HashMap();
        testMap.put("Test1", "TestObject1");
        testMap.put("Test2", "TestObject2");
        testMap.put("Test3", "TestObject3");
        testMap.put("Test", "TestObject");
        List resultList = SearchUtil.getMatchingObjects(testMap, "Test", true, true, false);
        assertTrue(resultList.size() == 1);
        assertTrue(resultList.contains("TestObject"));
        assertFalse(resultList.contains("TestObject1"));
        assertFalse(resultList.contains("TestObject2"));
        assertFalse(resultList.contains("TestObject3"));
        resultList = SearchUtil.getMatchingObjects(testMap, "Test", true, false, false);
        assertTrue(resultList.size() == 4);
        assertTrue(resultList.contains("TestObject"));
        assertTrue(resultList.contains("TestObject1"));
        assertTrue(resultList.contains("TestObject2"));
        assertTrue(resultList.contains("TestObject3"));
        resultList = SearchUtil.getMatchingObjects(testMap, "test", true, false, false);
        assertTrue(resultList.isEmpty());
        resultList = SearchUtil.getMatchingObjects(testMap, "test", false, false, false);
        assertTrue(resultList.size() == 4);
        assertTrue(resultList.contains("TestObject"));
        assertTrue(resultList.contains("TestObject1"));
        assertTrue(resultList.contains("TestObject2"));
        assertTrue(resultList.contains("TestObject3"));
        List testList = new ArrayList();
        testList.add("TestList1");
        testList.add("TestList2");
        testList.add("TestList3");
        testMap.put("Test4", testList);
        resultList = SearchUtil.getMatchingObjects(testMap, "", true, false, false);
        assertTrue(resultList.size() == 7);
        assertTrue(resultList.contains("TestObject"));
        assertTrue(resultList.contains("TestObject1"));
        assertTrue(resultList.contains("TestObject2"));
        assertTrue(resultList.contains("TestObject3"));
        assertTrue(resultList.contains("TestList1"));
        assertTrue(resultList.contains("TestList2"));
        assertTrue(resultList.contains("TestList3"));
        resultList = SearchUtil.getMatchingObjects(testMap, "", true, false, true);
        assertTrue(resultList.isEmpty());
        resultList = SearchUtil.getMatchingObjects(testMap, "TestObjectObject", true, false, true);
        assertTrue(resultList.size() == 1);
        assertTrue(resultList.contains("TestObject"));
    }
    
    public void testContains()
    {
        ArrayList list = new ArrayList();
        list.add("TestString1");
        list.add("TestString2");
        list.add("TestString3");
        assertTrue(SearchUtil.contains(list, "TESTSTRING", false, false));
        assertFalse(SearchUtil.contains(list, "TESTSTRING", true, false));
        assertFalse(SearchUtil.contains(list, "TESTSTRING", false, true));
        assertTrue(SearchUtil.contains(list, "TestString3", true, true));
        assertFalse(SearchUtil.contains(list, "TestString4", true, true));
        assertFalse(SearchUtil.contains(list, "TestString4", false, false));
    }
    
    public void testDoesStringMatch()
    {
        assertFalse(SearchUtil.doesStringMatch("X", "x", true, false));
        assertTrue(SearchUtil.doesStringMatch("X", "x", false, true));
        assertTrue(SearchUtil.doesStringMatch("Test", "tes", false, false));
        assertFalse(SearchUtil.doesStringMatch("Test", "tes", true, false));
        assertTrue(SearchUtil.doesStringMatch("Test", "", true, false));
        assertTrue(SearchUtil.doesStringMatch("Test", null, true, false));
        assertFalse(SearchUtil.doesStringMatch("Test", null, false, true));
        assertTrue(SearchUtil.doesStringMatch(null, null, true, true));
        assertTrue(SearchUtil.doesStringMatch("ThisIsATest", "ThisIsATest", true, true));
    }
}
