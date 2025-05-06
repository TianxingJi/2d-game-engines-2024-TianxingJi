package debugger.collisions;

import debugger.support.Vec2d;
import debugger.support.interfaces.Week2Reqs;

/**
 * Fill this class in during Week 2.
 */
public final class Week2 extends Week2Reqs {

	// AXIS-ALIGNED BOXES

	@Override
	public boolean isColliding(AABShape s1, AABShape s2) {
		Vec2d s1BottomRight = s1.getTopLeft().plus(s1.getSize());
		Vec2d s2BottomRight = s2.getTopLeft().plus(s2.getSize());

		boolean noOverlapX = s1BottomRight.x <= s2.getTopLeft().x || s2BottomRight.x <= s1.getTopLeft().x;
		boolean noOverlapY = s1BottomRight.y <= s2.getTopLeft().y || s2BottomRight.y <= s1.getTopLeft().y;

		return !(noOverlapX || noOverlapY);
	}


	@Override
	public boolean isColliding(AABShape s1, CircleShape s2) {
		Vec2d circleCenter = s2.getCenter();
		float radius = s2.getRadius();

		// Find the closest point to the circle within the AABB
		double closestX = Math.max(s1.getTopLeft().x, Math.min(circleCenter.x, s1.getTopLeft().x + s1.getSize().x));
		double closestY = Math.max(s1.getTopLeft().y, Math.min(circleCenter.y, s1.getTopLeft().y + s1.getSize().y));

		// Calculate the distance from the circle's center to this closest point
		double distanceX = circleCenter.x - closestX;
		double distanceY = circleCenter.y - closestY;

		// If the distance is less than the radius, there's a collision
		return (distanceX * distanceX + distanceY * distanceY) < (radius * radius);
	}


	@Override
	public boolean isColliding(AABShape s1, Vec2d point) {
		return point.x >= s1.getTopLeft().x && point.x <= s1.getTopLeft().x + s1.getSize().x &&
				point.y >= s1.getTopLeft().y && point.y <= s1.getTopLeft().y + s1.getSize().y;
	}


	// CIRCLES

	@Override
	public boolean isColliding(CircleShape s1, AABShape s2) {
		return isColliding(s2, s1);
	}

	@Override
	public boolean isColliding(CircleShape s1, CircleShape s2) {
		double distanceX = s1.getCenter().x - s2.getCenter().x;
		double distanceY = s1.getCenter().y - s2.getCenter().y;
		double distanceSquared = distanceX * distanceX + distanceY * distanceY;

		float radiusSum = s1.getRadius() + s2.getRadius();
		return distanceSquared < (radiusSum * radiusSum);
	}

	@Override
	public boolean isColliding(CircleShape s1, Vec2d point) {
		double distanceX = s1.getCenter().x - point.x;
		double distanceY = s1.getCenter().y - point.y;

		return (distanceX * distanceX + distanceY * distanceY) <= (s1.getRadius() * s1.getRadius());
	}



}
