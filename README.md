eap-to-eap-ejb-invocation: Remote EJB Invocation from Separate Instance Example
=====================================
Author: Red Hat Consulting  
Level: Intermediate  
Technologies: EJB
Summary: The `eap-to-eap-ejb-invocation` quickstart uses *EJB* to demonstrate how to access an EJB, deployed to JBoss EAP, from a remote EAP instance.
Target Product: JBoss EAP  

System requirements
-------------------

The application this project produces is designed to be run on Red Hat JBoss Enterprise Application Platform 7 or later.

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later. See [Configure Maven for JBoss EAP 7](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN_JBOSS_EAP7.md#configure-maven-to-build-and-deploy-the-quickstarts) to make sure you are configured correctly for testing the quickstarts.

What is it?
-----------

The purpose of this `eap-to-eap-ejb-invocation` quickstart is to demonstrate how to lookup and invoke EJBs deployed on an EAP7 server instance from another EAP7 server instance. This is different from invoking the EJBs from a remote standalone client

Let's call the server, from which the invocation happens to the EJB, as "Client Server" and the server on which the bean is deployed as the "Destination Server".

    Note: that this chapter deals with the case where the bean is deployed on the "Destination Server" but not on the "Client Server".

There are two components to this example:

1. Destination Server:

    The server component is comprised of a stateful EJB. It provides both an EJB JAR that is deployed to the server and a JAR file containing the remote business interfaces required by the remote client.

        jboss-ejb-remote-server-side-instance
        |---src/main/java
        |   |---org.jboss.as.quickstarts.ejb.remote.stateful
        |   |   |---ComplimenterEJB.java
        |   |   |---RemoteComplimenterEJB.java

2. Client Server that accesses the Destination Server component.

    The remote client instance depends on the remote business interfaces from the server component. This application looks up the stateful beans via the @EJB annotation and invokes a number of methods on them.

        jboss-ejb-in-war-remote-instance
        |---src/main/java
        |   |---org.jboss.as.quickstarts.ejbinwar.remoteinstance.controller
        |   |   |---Complimenter.java
        |   |---webapp
        |   |   |---index.html
        |   |   |---complimenter.xhtml
        |   |   |---WEB-INF
        |   |   |   |---beans.xml
        |   |   |   |---faces-config.xml
        |   |   |   |---jboss-ejb-client.xml


Each component is defined in its own standalone Maven module.

Beans
------------------
In our example, we'll consider a simple stateful session bean which is as follows:

  `RemoteComplimenterEJB.java`

    package org.jboss.as.quickstarts.ejb.remote.stateful;

    public interface RemoteComplimenterEJB {
      String compliment(String name);
    }

  `ComplimenterEJB.java`

    package org.jboss.as.quickstarts.ejb.remote.stateful;

    import javax.ejb.Remote;
    import javax.ejb.Stateful;

    @Stateful
    @Remote(RemoteComplimenterEJB.class)
    public class ComplimenterEJB implements RemoteComplimenterEJB {

    	@Override
    	public String compliment(String name) {
    		return "Looking Good, " + name;
    	}
    }


Use of EAP7_HOME
---------------

In the following instructions, replace `EAP7_HOME` with the actual path to your JBoss EAP installation. The installation path is described in detail here: [Use of EAP7_HOME and JBOSS_HOME Variables](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/USE_OF_EAP7_HOME.md#use-of-eap_home-and-jboss_home-variables).


Security
--------------------

JBoss EAP7 is secure by default. What this means is that no communication can happen with an EAP7 instance from a remote client (irrespective of whether it is a standalone client or another server instance) without passing the appropriate credentials. Remember that in this example, our "client server" will be communicating with the "destination server". So in order to allow this communication to happen successfully, we'll have to configure user credentials which we will be using during this communication. So let's start with the necessary configurations for this.

Configuring a user on the "Destination Server"
---------------------

As a first step we'll configure a user on the destination server who will be allowed to access the destination server. We create the user using the add-user script that's available in the EAP7_HOME/bin folder. In this example, we'll be configuring a Application User named ejb and with a password test in the ApplicationRealm. Running the add-user script is an interactive process and you will see questions/output as follows:

1. Run the `add-user` script (server is not required to be started)
    EAP7_HOME/bin/add-user.bat

2. Choose option `b` to create an `Application User`
3. Press enter to keep the defaults for the following screens, except for username/pw, which you may choose freely, and type 'y' when asked: `Is this new user going to be used for one AS process to connect to another AS process e.g. slave domain controller?`
4. Upon completion, a base64 encoding of your password will be generated. Record this value as it will be used later. For example: `dGVzdA==`


Start the 'Destination Server'
-------------------------

1. Open a command prompt and navigate to the root of the JBoss EAP directory.
2. The following shows the command line to start the server:

        For Linux:   EAP7_HOME/bin/standalone.sh -Djboss.node.name=destination-server
        For Windows: EAP7_HOME\bin\standalone.bat -Djboss.node.name=destination-server


        Note: It's very important to note that if you are starting both the server instances on the same machine, then each of those server instances must have a unique jboss.node.name system property. You can do that by passing an appropriate value for -Djboss.node.name system property to the startup script:
      ./standalone.sh -server-config=standalone-full.xml -Djboss.node.name=<add appropriate value here>



Build and Deploy the 'Destination Server'
-------------------------

Since this quickstart builds two separate components, you can not use the standard *Build and Deploy* commands used by most of the other quickstarts. You must follow these steps to build, deploy, and run this quickstart.

1. Make sure you have started the JBoss EAP server. See the instructions in the previous section.
2. Open a command prompt and navigate to the ejb-remote quickstart directory
3. Build and install the server side component:
    * Navigate to the `jboss-ejb-remote-server-side-instance` subdirectory:

            cd jboss-ejb-remote-server-side-instance
    * Build the EJB and client interfaces JARs and install them in your local Maven repository.

            mvn clean install        
    * Deploy the EJB JAR to your server. This Maven goal will deploy `jboss-ejb-remote-server-side-instance/target/jboss-ejb-remote-server-side-instance.jar`. You can check the JBoss EAP server console to see information messages regarding the deployment.

4. Deploy the server jar to the server:
  * In a browser window, navigate to http://localhost:9990/

          Note: change to server's ip if accessing remotely
  * Click on the `deployments` tab and select `add`.

  * Leave the default as `Upload a new deployment` and select `next`.

  * Browse and select `jboss-ejb-remote-server-side-instance.jar` and select `next`.

  * Continue selecting `next`, leaving the defaults and `finish`.


Configuring the "Client Server" to point to the EJB remoting connector on the "Destination Server"
---------------

As a first step on the "Client Server", we need to let the server know about the "Destination Server"'s EJB remoting connector, over which it can communicate during the EJB invocations. To do that, we'll have to add a "remote-outbound-connection" to the remoting subsystem on the "Client Server". The "remote-outbound-connection" configuration indicates that a outbound connection will be created to a remote server instance from that server. The "remote-outbound-connection" will be backed by a "outbound-socket-binding" which will point to a remote host and a remote port (of the "Destination Server"). So let's see how we create these configurations.


Start the JBoss EAP Server
-------------------------
In this example, we'll start the "Client Server" on the same machine as the "Destination Server". We have copied the entire server installation to a different folder and while starting the "Client Server" we'll use a port-offset (of 100 in this example) to avoid port conflicts:

1. Open a command prompt and navigate to the root of the JBoss EAP directory.
2. The following shows the command line to start the server:

        For Linux:   EAP7_HOME/bin/standalone.sh -Djboss.socket.binding.port-offset=100
        For Windows: EAP7_HOME\bin\standalone.bat -Djboss.socket.binding.port-offset=100


Create a security realm on the client server
-------------------------
Remember that we need to communicate with a secure destination server. In order to do that the client server has to pass the user credentials to the destination server. Earlier we created a user on the destination server who'll be allowed to communicate with that server. Now on the "client server" we'll create a security-realm which will be used to pass the user information.

In this example we'll use a security realm which stores a Base64 encoded password and then passes on that credentials when asked for. Earlier we created a user named ejb and password test. So our first task here would be to create the base64 encoded version of the password test. You can use any utility which generates you a base64 version for a string. I used this online site which generates the base64 encoded string. So for the test password, the base64 encoded version is dGVzdA==

    Note: While generating the base64 encoded string make sure that you don't have any trailing or leading spaces for the original password. That can lead to incorrect encoded versions being generated.

Now that we have generated that base64 encoded password, let's use in the in the security realm that we are going to configure on the "client server". I'll first shutdown the client server and edit the standlaone-full.xml file to add the following in the <management> section

Now let's create a "security-realm" for the base64 encoded password.  

    /core-service=management/security-realm=ejb-security-realm:add()
    /core-service=management/security-realm=ejb-security-realm/server-identity=secret:add(value=dGVzdA==)

Upon successful invocation of this command, the following configuration will be created in the management section:

    'standalone.xml'

      <management>
              <security-realms>
                  ...
                  <security-realm name="ejb-security-realm">
                      <server-identities>
                          <secret value="dGVzdA=="/>
                      </server-identities>
                  </security-realm>
              </security-realms>
      ...

As you can see I have created a security realm named "ejb-security-realm" (you can name it anything) with the base64 encoded password. So that completes the security realm configuration for the client server. Now let's move on to the next step.

Create a outbound-socket-binding on the "Client Server"
-------------------------

Let's first create a outbound-socket-binding which points the "Destination Server"'s host and port. We'll use the CLI to create this configuration:

    /socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=remote-ejb:add(host=localhost, port=4447)
The above command will create a outbound-socket-binding named "remote-ejb" (we can name it anything) which points to "localhost" as the host and port 4447 as the destination port. Note that the host information should match the host/IP of the "Destination Server" (in this example we are running on the same machine so we use "localhost") and  the port information should match the remoting connector port used by the EJB subsystem (by default it's 4447). When this command is run successfully, we'll see that the standalone-full.xml (the file which we used to start the server) was updated with the following outbound-socket-binding in the socket-binding-group:

    <socket-binding-group name="standard-sockets" default-interface="public" port-offset="${jboss.socket.binding.port-offset:0}">
            ...
            <outbound-socket-binding name="remote-ejb">
                <remote-destination host="localhost" port="4447"/>
            </outbound-socket-binding>
        </socket-binding-group>

Create a "remote-outbound-connection" which uses this newly created "outbound-socket-binding"
-----------------------------------
Now let's create a "remote-outbound-connection" which will use the newly created outbound-socket-binding (pointing to the EJB remoting connector of the "Destination Server"). We'll continue to use the CLI to create this configuration:

    /subsystem=remoting/remote-outbound-connection=remote-ejb-connection:add(outbound-socket-binding-ref=remote-ejb, security-realm=ejb-security-realm, username=ejb)

The above command creates a remote-outbound-connection, named "remote-ejb-connection" (we can name it anything), in the remoting subsystem and uses the previously created "remote-ejb" outbound-socket-binding (notice the outbound-socket-binding-ref in that command). Furthermore, we also set the security-realm attribute to point to the security-realm that we created in the previous step. Also notice that we have set the username attribute to use the user name who is allowed to communicate with the destination server.

What this step does is, it creates a outbound connection, on the client server, to the remote destination server and sets up the username to the user who allowed to communicate with that destination server and also sets up the security-realm to a pre-configured security-realm capable of passing along the user credentials (in this case the password). This way when a connection has to be established from the client server to the destination server, the connection creation logic will have the necessary security credentials to pass along and setup a successful secured connection.

Now let's run the following two operations to set some default connection creation options for the outbound connection:

    /subsystem=remoting/remote-outbound-connection=remote-ejb-connection/property=SASL_POLICY_NOANONYMOUS:add(value=false
    /subsystem=remoting/remote-outbound-connection=remote-ejb-connection/property=SSL_ENABLED:add(value=false)

Ultimately, upon successful invocation of this command, the following configuration will be created in the remoting subsystem:

    <subsystem xmlns="urn:jboss:domain:remoting:1.1">
    ....
                <outbound-connections>
                    <remote-outbound-connection name="remote-ejb-connection" outbound-socket-binding-ref="remote-ejb" security-realm="ejb-security-realm" username="ejb">
                        <properties>
                            <property name="SASL_POLICY_NOANONYMOUS" value="false"/>
                            <property name="SSL_ENABLED" value="false"/>
                        </properties>
                    </remote-outbound-connection>
                </outbound-connections>
            </subsystem>
From a server configuration point of view, that's all we need on the "Client Server". Our next step is to deploy an application on the "Client Server" which will invoke on the bean deployed on the "Destination Server".


In the client application we'll use a servlet which invokes on the bean deployed on the "Destination Server". We can even invoke the bean on the "Destination Server" from a EJB on the "Client Server". The code remains the same (JNDI lookup, followed by invocation on the proxy). The important part to notice in this client application is the file jboss-ejb-client.xml which is packaged in the META-INF folder of a top level deployment (in this case our client-app.ear). This jboss-ejb-client.xml contains the EJB client configurations which will be used during the EJB invocations for finding the appropriate destinations (also known as, EJB receivers). The contents of the jboss-ejb-client.xml are explained next.

	Note: If your application is deployed as a top level .war deployment, then the jboss-ejb-client.xml is expected to be placed in .war/WEB-INF/ folder (i.e. the same location where you place any web.xml file).

  Contents on jboss-ejb-client.xml
  --------------------------------
  The jboss-ejb-client.xml will look like:

    <jboss-ejb-client xmlns="urn:jboss:ejb-client:1.0">
        <client-context>
            <ejb-receivers>
                <remoting-ejb-receiver outbound-connection-ref="remote-ejb-connection"/>
            </ejb-receivers>
        </client-context>
    </jboss-ejb-client>

  You'll notice that we have configured the EJB client context (for this application) to use a remoting-ejb-receiver which points to our earlier created "remote-outbound-connection" named "remote-ejb-connection". This links the EJB client context to use the "remote-ejb-connection" which ultimately points to the EJB remoting connector on the "Destination Server".


Build and run the client instance
-------------------------------------------------

1. Navigate to the client subdirectory:

        cd jboss-ejb-in-war-remote-instance

2. Build the client web application

        mvn clean install

3. In your browser, navigate to `http://localhost:8180/jboss-ejb-in-war-remote-instance/`
