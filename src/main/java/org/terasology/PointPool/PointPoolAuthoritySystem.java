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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.PointPool.event.DrainPoolEvent;
import org.terasology.PointPool.event.InstantDrainEvent;
import org.terasology.PointPool.event.InstantFillEvent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.PointPool.event.FillPoolEvent;

@RegisterSystem(RegisterMode.AUTHORITY)
public class PointPoolAuthoritySystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(PointPoolAuthoritySystem.class);

    @ReceiveEvent
    public void onFillPool(FillPoolEvent event, EntityRef entity, PointPoolComponent poolComponent) {
        poolComponent.poolValue += event.getValue();
        if (poolComponent.poolValue > poolComponent.maxPoolValue) {
            poolComponent.poolValue = poolComponent.maxPoolValue;
        }
        event.getInstigator().saveComponent(poolComponent);
        logger.info("Current status " + poolComponent.poolValue);
    }

    @ReceiveEvent
    public void drainPool(DrainPoolEvent event, EntityRef entity, PointPoolComponent pointPoolComponent) {
        pointPoolComponent.poolValue -= event.getValue();
        if (pointPoolComponent.poolValue < 0) {
            pointPoolComponent.poolValue = 0;
        }
        entity.saveComponent(pointPoolComponent);
        logger.info("Current status " + pointPoolComponent.poolValue);
    }

    @ReceiveEvent
    public void instantDrain(InstantDrainEvent event, EntityRef entity, PointPoolComponent pointPoolComponent) {
        pointPoolComponent.poolValue = 0;
        entity.saveComponent(pointPoolComponent);
        logger.info("Current status " + pointPoolComponent.poolValue);
    }

    @ReceiveEvent
    public void instantFill(InstantFillEvent event, EntityRef entity, PointPoolComponent pointPoolComponent) {
        pointPoolComponent.poolValue = pointPoolComponent.maxPoolValue;
        entity.saveComponent(pointPoolComponent);
        logger.info("Current status " + pointPoolComponent.poolValue);
    }

}
