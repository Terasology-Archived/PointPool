/*
 * Copyright 2019 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.PointPool;

import org.terasology.PointPool.event.DrainPoolEvent;
import org.terasology.PointPool.event.FillPoolEvent;
import org.terasology.PointPool.event.InstantDrainEvent;
import org.terasology.PointPool.event.InstantFillEvent;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.console.commandSystem.annotations.Command;
import org.terasology.logic.console.commandSystem.annotations.CommandParam;
import org.terasology.logic.console.commandSystem.annotations.Sender;
import org.terasology.logic.permission.PermissionManager;
import org.terasology.network.ClientComponent;
import org.terasology.registry.Share;

@RegisterSystem(RegisterMode.AUTHORITY)
@Share(PointPoolCommands.class)
public class PointPoolCommands extends BaseComponentSystem {

    @Command(value = "fillPool", shortDescription = "Fill pool by amount given",
            requiredPermission = PermissionManager.NO_PERMISSION)
    public String fill(@Sender EntityRef client, @CommandParam("amount") float amount) {
        ClientComponent clientComp = client.getComponent(ClientComponent.class);

        clientComp.character.send(new FillPoolEvent(amount, clientComp.character));

        return "Pool filled by " + amount;
    }

    @Command(value = "drainPool", shortDescription = "Drain pool by amount given",
            requiredPermission = PermissionManager.NO_PERMISSION)
    public String drainPool(@Sender EntityRef client, @CommandParam("amount") float amount) {
        ClientComponent clientComp = client.getComponent(ClientComponent.class);
        clientComp.character.send(new DrainPoolEvent(amount, clientComp.character));
        return "Pool drained by " + amount;
    }

    @Command(value = "instantDrain", shortDescription = "Instantly drain the given pool",
            requiredPermission = PermissionManager.NO_PERMISSION)
    public String instantDrain(@Sender EntityRef client, @CommandParam("type") String type) {
        ClientComponent clientComp = client.getComponent(ClientComponent.class);
        clientComp.character.send(new InstantDrainEvent(type, clientComp.character));
        return type + " Pool drained completely";
    }

    @Command(value = "instantFill", shortDescription = "Instantly fills the given pool",
            requiredPermission = PermissionManager.NO_PERMISSION)
    public String instantFill(@Sender EntityRef client, @CommandParam("type") String type) {
        ClientComponent clientComp = client.getComponent(ClientComponent.class);
        clientComp.character.send(new InstantFillEvent(type, clientComp.character));
        return type + " Pool filled completely";
    }

    @Command(value = "listComponents", shortDescription = "Lists all components of character",
            requiredPermission = PermissionManager.NO_PERMISSION)
    public String list(@Sender EntityRef client) {
        String s = "";
        for (Component component : client.getComponent(ClientComponent.class).character.iterateComponents()) {
            s += component + "\n";
        }
        return s;
    }

    @Command(value = "poolStatus", shortDescription = "Get the status of pool with given type", runOnServer = true,
            requiredPermission = PermissionManager.NO_PERMISSION)
    public String status(@Sender EntityRef client, @CommandParam("type") String type) {
        ClientComponent clientComp = client.getComponent(ClientComponent.class);
        // TODO : Make this work for multiple pools attached to same entity
        // for(PointPoolComponent component : clientComp.character.getComponent(PointPoolComponent.class)
        try {
            float value = clientComp.character.getComponent(PointPoolComponent.class).poolValue;
            return "Current pool value is " + value;
        } catch (NullPointerException n) {
            return "NullPointerException encountered";
        }
    }

}
