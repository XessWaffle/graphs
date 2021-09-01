package base;

import java.util.HashSet;
import java.util.Objects;

public class Component {

    private HashSet<Node> component;
    private int componentId;

    public Component(int componentId){
        component = new HashSet<>();
        this.componentId = componentId;
    }

    public void addNode(Node next){
        component.add(next);
        next.setComponent(this);
    }

    public int getComponentId(){
        return componentId;
    }

    public void merge(Component other){
        this.component.addAll(other.component);
    }


    public Node getRandomVertex() {
        int index = (int)(Math.random()) * component.size(), i = 0;

        for(Node next: component){
            if(i == index){
                return next;
            }
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Component component = (Component) o;
        return componentId == component.componentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentId);
    }
}

