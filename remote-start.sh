jar_path=<path_to_jar>
user=chipmunk
domain=cs.colostate.edu
registry_host=<registry_hostname>
registry_port=<registry_server_socket_port>
machine_list=./machine_list

for machine in 'cat $machine_list'
do
    gnome-terminal -x bash -c "ssh -t $user@$machine.$domain 'java -cp $jar_path cs455.overlay.node.MessagingNode $registry_host $registry_port'"
done