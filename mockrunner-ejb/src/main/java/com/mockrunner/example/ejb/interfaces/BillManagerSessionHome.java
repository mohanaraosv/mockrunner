/*
 * Generated by XDoclet - Do not edit!
 */
package com.mockrunner.example.ejb.interfaces;

/**
 * Home interface for BillManagerSession.
 */
public interface BillManagerSessionHome
   extends javax.ejb.EJBHome
{
   String COMP_NAME="java:comp/env/ejb/BillManagerSession";
   String JNDI_NAME="com/mockrunner/example/BillManagerSession";

   com.mockrunner.example.ejb.interfaces.BillManagerSession create()
      throws javax.ejb.CreateException,java.rmi.RemoteException;

}