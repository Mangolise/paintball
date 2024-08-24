package net.mangolise.paintball;

import dev.emortal.rayfast.area.area3d.Area3d;
import dev.emortal.rayfast.area.area3d.Area3dRectangularPrism;
import dev.emortal.rayfast.vector.Vector3d;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;

public class InitRayfast {

    public static void init() {
        Vector3d.CONVERTER.register(Vec.class, vec -> Vector3d.of(vec.x(), vec.y(), vec.z()));
        Vector3d.CONVERTER.register(Point.class, point -> Vector3d.of(point.x(), point.y(), point.z()));
        Vector3d.CONVERTER.register(Pos.class, pos -> Vector3d.of(pos.x(), pos.y(), pos.z()));
        Area3d.CONVERTER.register(BoundingBox.class, box -> Area3dRectangularPrism.of(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ()));
        Area3d.CONVERTER.register(Entity.class, entity -> {
            Point position = entity.getPosition();
            BoundingBox boundingBox = entity.getBoundingBox();
            return Area3d.CONVERTER.from(boundingBox.withOffset(position));
        });

    }
}
