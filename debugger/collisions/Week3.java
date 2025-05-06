package debugger.collisions;

import debugger.support.Vec2d;
import debugger.support.interfaces.Week3Reqs;

/**
 * Fill this class in during Week 3. Make sure to also change the week variable in Display.java.
 */
public final class Week3 extends Week3Reqs {

	// AXIS-ALIGNED BOXES

	@Override
	public Vec2d collision(AABShape s1, AABShape s2) {
		Vec2d s1Min = s1.getTopLeft();
		Vec2d s1Max = s1Min.plus(s1.getSize());
		Vec2d s2Min = s2.getTopLeft();
		Vec2d s2Max = s2Min.plus(s2.getSize());

		// Calculate overlap on x and y axes
		double overlapX = Math.max(0, Math.min(s1Max.x - s2Min.x, s2Max.x - s1Min.x));
		double overlapY = Math.max(0, Math.min(s1Max.y - s2Min.y, s2Max.y - s1Min.y));

		// If no overlap on either axis, return null (no collision)
		if (overlapX <= 0 || overlapY <= 0) {
			return null;
		}

		// Move along the smallest overlap axis
		if (overlapX < overlapY) {
			// Move along the x-axis
			if (s1Min.x < s2Min.x) {
				return new Vec2d(-overlapX, 0); // Move left
			} else {
				return new Vec2d(overlapX, 0); // Move right
			}
		} else {
			// Move along the y-axis
			if (s1Min.y < s2Min.y) {
				return new Vec2d(0, -overlapY); // Move up
			} else {
				return new Vec2d(0, overlapY); // Move down
			}
		}
	}


	@Override
	public Vec2d collision(AABShape s1, CircleShape s2) {
		Vec2d circleCenter = s2.getCenter();
		double radius = s2.getRadius();

		// Get the bounds of the AABB
		Vec2d s1Min = s1.getTopLeft();
		Vec2d s1Max = s1Min.plus(s1.getSize());

		// Calculate the closest point on the AABB manually
		double closestX = Math.max(s1Min.x, Math.min(circleCenter.x, s1Max.x));
		double closestY = Math.max(s1Min.y, Math.min(circleCenter.y, s1Max.y));

		Vec2d closestPoint = new Vec2d(closestX, closestY);
		double distance = closestPoint.dist(circleCenter);

		// Case 1: Circle center is inside the AABB
		if (s1Min.x <= circleCenter.x && circleCenter.x <= s1Max.x &&
				s1Min.y <= circleCenter.y && circleCenter.y <= s1Max.y) {

			// Find the closest point on the edge of the AABB
			double distanceToTop = Math.abs(circleCenter.y - s1Min.y); // Distance to top edge
			double distanceToBottom = Math.abs(circleCenter.y - s1Max.y); // Distance to bottom edge
			double distanceToLeft = Math.abs(circleCenter.x - s1Min.x); // Distance to left edge
			double distanceToRight = Math.abs(circleCenter.x - s1Max.x); // Distance to right edge

			// Determine the closest edge and calculate MTV
			if (Math.min(distanceToLeft, distanceToRight) < Math.min(distanceToTop, distanceToBottom)) {
				// Closer to left or right edge
				if (distanceToLeft < distanceToRight) {
					closestX = s1Min.x; // Closest point is on the left edge
				} else {
					closestX = s1Max.x; // Closest point is on the right edge
				}
				closestY = circleCenter.y; // Keep the y-coordinate unchanged
			} else {
				// Closer to top or bottom edge
				if (distanceToTop < distanceToBottom) {
					closestY = s1Min.y; // Closest point is on the top edge
				} else {
					closestY = s1Max.y; // Closest point is on the bottom edge
				}
				closestX = circleCenter.x; // Keep the x-coordinate unchanged
			}

			// Recalculate distance based on the edge point
			closestPoint = new Vec2d(closestX, closestY);
			distance = closestPoint.dist(circleCenter);

			// Calculate the MTV
			double overlap = radius + distance;
			Vec2d direction = closestPoint.minus(circleCenter).normalize(); // Push away from the edge
			return direction.smult(-overlap); // MTV vector
		}

		// Case 2: Circle center is outside AABB
		if (distance < radius) {
			// Calculate the MTV
			double overlap = radius - distance;
			Vec2d direction = closestPoint.minus(circleCenter).normalize();
			return direction.smult(overlap); // MTV vector to separate the circle from the AABB
		}

		return null; // No collision
	}


	@Override
	public Vec2d collision(AABShape s1, Vec2d s2) {
		// Check if the point is inside the AABB
		Vec2d s1Min = s1.getTopLeft();
		Vec2d s1Max = s1Min.plus(s1.getSize());

		if (s2.x < s1Min.x || s2.x > s1Max.x || s2.y < s1Min.y || s2.y > s1Max.y) {
			return null; // No collision
		}

		double dx = Math.min(s1Max.x - s2.x, s2.x - s1Min.x);
		double dy = Math.min(s1Max.y - s2.y, s2.y - s1Min.y);

		if (dx < dy) {
			return new Vec2d(dx, 0); // Resolve along x-axis
		} else {
			return new Vec2d(0, dy); // Resolve along y-axis
		}
	}


	// CIRCLES

	@Override
	public Vec2d collision(CircleShape s1, AABShape s2) {
		Vec2d f = collision(s2, s1);
		return f == null ? null : f.reflect();
	}

	@Override
	public Vec2d collision(CircleShape s1, CircleShape s2) {
		// Calculate the direction and overlap between the two circles
		Vec2d direction = s1.getCenter().minus(s2.getCenter()); // Reverse the direction
		double distance = direction.mag();
		double radiusSum = s1.getRadius() + s2.getRadius();
		double overlap = radiusSum - distance;

		if (overlap > 0) {
			return direction.normalize().smult(overlap); // Return MTV
		}
		return null; // No collision
	}


	@Override
	public Vec2d collision(CircleShape s1, Vec2d s2) {
		// Calculate the distance between the circle center and the point
		Vec2d circleCenter = s1.getCenter();
		double distance = circleCenter.dist(s2);

		if (distance < s1.getRadius()) {
			double overlap = s1.getRadius() - distance;
			Vec2d direction = s2.minus(circleCenter).normalize(); // Reverse the direction
			return direction.smult(overlap); // Return MTV
		}
		return null; // No collision
	}

}
