Author: Reece Sharp
Project: Routing Packets Within a Structured Peer-to-Peer (P2P) Network Overlay

Quick Start:
1. Untar and go into new directory
2. To build:
    'gradle build'
3. To run:
    'cd build/libs'
    'java -cp Sharp_Reece_ASG1.jar cs455.overlay.node.Registry portnum'
    'java -cp Sharp_Reece_ASG1.jar cs455.overlay.node.MessagingNode registry-host registry-port'

Note: The Registry must be run first for the MessagingNodes to connect to. It will also output the host and port it is running on
if not already known. The MessagingNode startup also only creates 1 Node, it must be run multiple times


File Structure:
******
|-src
    |-main
        |-java
            |-cs455
                |-overlay
                    |-node
                        |- MessagingNode.java
                        |- Node.java
                        |- NodeData.java
                        |- NodeList.java
                        |- Registry.java
                    |-routing
                        |- RoutingEntry.java
                        |- RoutingTable.java
                    |-transport
                        |- TCPReceiverThread.java
                        |- TCPSenderThread.java
                        |- TCPServerThread.java
                    |-util
                        |- Commands.java
                        |- InteractiveCommandParser.java
                        |- StatisticsCollectorAndDisplay.java
                    |-wireformats
                        |- Event.java
                        |- EventFactory.java
                        |- NodeReportsOverlaySetupStatus.java
                        |- OverlayNodeReportsTaskFinished.java
                        |- OverlayNodeReportsTrafficSummary.java
                        |- OverlayNodeSendsRegistration.java
                        |- OverlayNodeSendsDeregistration.java
                        |- OverlayNodeSendsData.java
                        |- Protocol.java
                        |- RegistryReportsDeregistrationStatus.java
                        |- RegistryReportsRegistrationStatus.java
                        |- RegistryRequestsTaskInitiate.java
                        |- RegistryRequestsTrafficSummary.java
                        |- RegistrySendsNodeManifest.java
|-build.gradle
|-Readme.txt

File Descriptions
******
|- MessagingNode.java
    - The client. It pings the Registry every now and then for organization
|- Node.java
    - An interface both the MessagingNode and Registry use
|- NodeData.java
    - The NodeList utilizes this data structure for registered nodes 
|- NodeList.java
    - a list of currently registered nodes
|- Registry.java
    - The organizer of all of the nodes. It stores master lists of the MessagingNodes
    and sends out messages as necessary them. A node itself.

|- RoutingEntry.java
    - A datastructure utilized by the RoutingTable
|- RoutingTable.java
    - The MessagingNodes all hold a partial (and unique) table of other nodes
    - that together allow them to be completely connected

|- TCPReceiverThread.java
    - The listener, is spawned whenever the server receives a connection from somewhere else.
    Watches for information given by the TCPSenderThread
|- TCPSenderThread.java
    - Takes a byteArray and pushes it through a socket 
|- TCPServerThread.java
    - Each node utilizes this to listen for incoming connections from other nodes

|- Commands.java
    - Holds constants for the InteractiveCommandParser
|- InteractiveCommandParser.java
    - A thread in each node that handles user-input
|- StatisticsCollectorAndDisplay.java
    - Collects and organizes the output from each node after it's given to the Registry

|- Event.java
    - An interface that rest of the Events below use
|- EventFactory.java
    - A simpleton type Event constructor
|- NodeReportsOverlaySetupStatus.java
    - An Event. Sent to Registry when the node is successful/unsuccessful at setting up its overlay connections
|- OverlayNodeReportsTaskFinished.java
    - An Event. Sent to Registry when the node finishes its task
|- OverlayNodeReportsTrafficSummary.java
    - An Event. Response to Registry with its results after running
|- OverlayNodeSendsRegistration.java
    - An Event. Inital message sent. Goes to Registry
|- OverlayNodeSendsDeregistration.java
    - An Event. Sent by MessagingNode when it wants to exit. Done before overlay is setup
|- OverlayNodeSendsData.java
    - An Event. Sent from MessagingNode to MessagingNode 
|- Protocol.java
    - Constants that bind a class to an integer
|- RegistryReportsDeregistrationStatus.java
    - An Event.
|- RegistryReportsRegistrationStatus.java
    - An Event.
|- RegistryRequestsTaskInitiate.java
    - An Event.
|- RegistryRequestsTrafficSummary.java
    - An Event.
|- RegistrySendsNodeManifest.java
    - An Event.
                        
|- build.gradle
    - File utilized by gradle to build the cs455 package