package com.valtech.taf.utilities;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import java.util.ArrayList;
import java.util.List;


/*
 * <h1>Docker class</h1>
 * A class to handle starting/stopping docker containers
 * @author M. Karpov
 * @author V. Chaudhari
 */
public class Docker {

    private static final String CONFIG = "/home/baeldung/.docker/";
    private static final String HOST = "tcp://localhost:2375";
    private DockerClient dockerClient;

    public Docker(){
        getClient();
    }

    /*
     * <h1> stopContainer method</h1>
     * A method to stop a docker container by name
     * @param name
     * @see uses DockerClient initialized with the class
     * @return none
     */
    public void stopContainer(String name){
        List<Container> containerList = dockerClient.listContainersCmd().exec();
        containerList.forEach((Container container) -> {if(container.getImage().contains(name)) {
            dockerClient.stopContainerCmd(container.getId()).exec();
        }
        });

    }

    /*
     * <h1>start Container method</h1>
     * A method to start a container by its name
     * @param name
     * @param file - the name of docker-compose file
     * @return none
     * @see depends on DockerClient object created when the class initialized
     * @see the container is initialized with data in docker-compose file
     */
    public void startContainer(String name){

        List<String> states = new ArrayList<>();
        states.add("exited");
        List<Container> list = dockerClient.listContainersCmd()
                .withShowSize(true)
                .withShowAll(true)
                .withStatusFilter(states).exec();
        for(Container container:list){
            if(container.getImage().contains(name)) {
                dockerClient.startContainerCmd(container.getId()).exec();
                break;
            }
        }


    }

    /*
     * <h1>getClient method</h1>
     * the method used by the class constructor to initialize the global DockerClient dockerClient variable
     * @param none
     * @return none
     *
     */
    private void getClient(){
        DefaultDockerClientConfig.Builder config
                = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerConfig(CONFIG)
                .withDockerHost(HOST);

        dockerClient = DockerClientBuilder
                .getInstance(config)
                .build();

    }

    /*
     * <h1>getContainers method</h1>
     * @param none
     * @return active Containers in the system
     */
    public List<Container> getContainers(){
        return dockerClient.listContainersCmd().exec();
    }

}
