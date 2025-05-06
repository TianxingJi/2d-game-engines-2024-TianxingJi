package debugger.collisions;

import debugger.support.Vec2d;
import debugger.support.interfaces.Week6Reqs;

/**
 * Fill this class in during Week 6. Make sure to also change the week variable in Display.java.
 */
public final class Week6 extends Week6Reqs {

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


	@Override
	public Vec2d collision(AABShape s1, PolygonShape s2) {
		int numAxes = 2 + s2.getNumPoints(); // 2 for AABB axes (x and y) + number of polygon edges
		Vec2d[] axes = new Vec2d[numAxes];
		int axisIndex = 0;

		// 1. Get AABB axes (x and y)
		axes[axisIndex++] = new Vec2d(1, 0); // X-axis
		axes[axisIndex++] = new Vec2d(0, 1); // Y-axis

		// 2. Get Polygon axes (its edges' normals)
		for (int i = 0; i < s2.getNumPoints(); i++) {
			Vec2d p1 = s2.getPoint(i);
			Vec2d p2 = s2.getPoint((i + 1) % s2.getNumPoints());
			Vec2d edge = p2.minus(p1);
			Vec2d normal = new Vec2d(-edge.y, edge.x).normalize(); // Perpendicular to edge
			axes[axisIndex++] = normal;
		}

		double minOverlap = Double.MAX_VALUE;
		Vec2d mtvAxis = null;

		// 3. Project both shapes onto each axis and check overlap
		for (Vec2d axis : axes) {
			// Project AABB onto axis
			double[] aabbProjection = projectAABB(s1, axis);
			double aabbMin = aabbProjection[0];
			double aabbMax = aabbProjection[1];

			// Project Polygon onto axis
			double[] polyProjection = projectPolygon(s2, axis);
			double polyMin = polyProjection[0];
			double polyMax = polyProjection[1];

			// Calculate overlap
			double overlap = Math.min(aabbMax, polyMax) - Math.max(aabbMin, polyMin);
			if (overlap <= 0) {
				// No overlap means no collision
				return null;
			}

			// Track the smallest overlap for MTV
			if (overlap < minOverlap) {
				minOverlap = overlap;
				mtvAxis = axis;
			}
		}

		// 4. Determine the direction of MTV
		Vec2d s1Center = s1.getTopLeft().plus(s1.getSize().smult(0.5)); // Center of AABB
		Vec2d s2Center = s2.getPoint(0);
		for (int i = 1; i < s2.getNumPoints(); i++) {
			s2Center = s2Center.plus(s2.getPoint(i));
		}
		s2Center = s2Center.smult(1.0 / s2.getNumPoints()); // Average to get center

		Vec2d centerToCenter = s2Center.minus(s1Center);

		// If the MTV points in the same direction as the centerToCenter vector, invert it.
		if (centerToCenter.dot(mtvAxis) > 0) {
			mtvAxis = mtvAxis.smult(-1);
		}

		// 5. Return the MTV vector (minimum overlap along the correct direction)
		return mtvAxis.smult(minOverlap);
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

	@Override
	public Vec2d collision(CircleShape s1, PolygonShape s2) {
		Vec2d circleCenter = s1.getCenter();
		double radius = s1.getRadius();
		int numPoints = s2.getNumPoints();

		// 1. Collect all potential axes (polygon edges' normals)
		Vec2d[] axes = new Vec2d[numPoints];
		for (int i = 0; i < numPoints; i++) {
			Vec2d p1 = s2.getPoint(i);
			Vec2d p2 = s2.getPoint((i + 1) % numPoints);
			Vec2d edge = p2.minus(p1);
			Vec2d normal = new Vec2d(-edge.y, edge.x).normalize(); // Perpendicular to edge
			axes[i] = normal;
		}

		// 2. Add an axis from the circle's center to the closest point on the polygon
		Vec2d closestPoint = null;
		double minDistance = Double.MAX_VALUE;
		for (int i = 0; i < numPoints; i++) {
			Vec2d point = s2.getPoint(i);
			double distance = circleCenter.dist2(point); // Using squared distance for efficiency
			if (distance < minDistance) {
				minDistance = distance;
				closestPoint = point;
			}
		}
		Vec2d difference = closestPoint.minus(circleCenter);
		Vec2d axisToCircle = difference.normalize();

		// Include this as a potential separating axis
		Vec2d[] allAxes = new Vec2d[numPoints + 1];
		System.arraycopy(axes, 0, allAxes, 0, numPoints);
		allAxes[numPoints] = axisToCircle;

		// 3. Project shapes onto each axis and check for overlap
		double minOverlap = Double.MAX_VALUE;
		Vec2d mtvAxis = null;

		for (Vec2d axis : allAxes) {
			// Project the circle onto the axis
			double[] circleProjection = projectCircle(s1, axis);
			double circleMin = circleProjection[0];
			double circleMax = circleProjection[1];

			// Project the polygon onto the axis
			double[] polyProjection = projectPolygon(s2, axis);
			double polyMin = polyProjection[0];
			double polyMax = polyProjection[1];

			// Calculate overlap
			double overlap = Math.min(circleMax, polyMax) - Math.max(circleMin, polyMin);
			if (overlap <= 0) {
				// No overlap means no collision
				return null;
			}

			// Track the smallest overlap for MTV
			if (overlap < minOverlap) {
				minOverlap = overlap;
				mtvAxis = axis;
			}
		}

		// 4. Adjust MTV direction to point away from the circle's center (opposite to collision)
		if (mtvAxis.dot(difference) > 0) {
			mtvAxis = mtvAxis.smult(-1); // Reverse the MTV direction to point away from the circle
		}

		// 5. Return the MTV vector (minimum overlap along the correct direction)
		return mtvAxis.smult(minOverlap);
	}

	// POLYGONS

	@Override
	public Vec2d collision(PolygonShape s1, AABShape s2) {
		Vec2d f = collision(s2, s1);
		return f == null ? null : f.reflect();
	}

	@Override
	public Vec2d collision(PolygonShape s1, CircleShape s2) {
		Vec2d f = collision(s2, s1);
		return f == null ? null : f.reflect();
	}

	@Override
	public Vec2d collision(PolygonShape s1, Vec2d s2) {
		int numPoints = s1.getNumPoints();
		boolean allPositive = true;
		boolean allNegative = true;

		for (int i = 0; i < numPoints; i++) {
			Vec2d p1 = s1.getPoint(i);
			Vec2d p2 = s1.getPoint((i + 1) % numPoints); // Loop to first point for last edge

			// Vector along the edge
			Vec2d edge = p2.minus(p1);
			// Vector from edge point to the target point
			Vec2d toPoint = s2.minus(p1);

			// Calculate the cross product (z-component of 2D cross product)
			double cross = edge.x * toPoint.y - edge.y * toPoint.x;

			if (cross > 0) {
				allNegative = false; // Not all are negative anymore
			} else if (cross < 0) {
				allPositive = false; // Not all are positive anymore
			}

			// Early exit: if signs are mixed, the point is outside
			if (!allPositive && !allNegative) {
				return null; // The point is outside the polygon
			}
		}

		// If all cross products are either positive or negative, the point is inside
		return new Vec2d(0, 0); // Return zero vector to indicate the point is inside
	}

	@Override
	public Vec2d collision(PolygonShape s1, PolygonShape s2) {
		int numPoints1 = s1.getNumPoints();
		int numPoints2 = s2.getNumPoints();

		// 1. Collect all potential axes (edges' normals of both polygons)
		Vec2d[] axes = new Vec2d[numPoints1 + numPoints2];
		int axisIndex = 0;

		// 1.1 Get axes from the edges of the first polygon
		for (int i = 0; i < numPoints1; i++) {
			Vec2d p1 = s1.getPoint(i);
			Vec2d p2 = s1.getPoint((i + 1) % numPoints1);
			Vec2d edge = p2.minus(p1);
			Vec2d normal = new Vec2d(-edge.y, edge.x).normalize(); // Perpendicular to edge
			axes[axisIndex++] = normal;
		}

		// 1.2 Get axes from the edges of the second polygon
		for (int i = 0; i < numPoints2; i++) {
			Vec2d p1 = s2.getPoint(i);
			Vec2d p2 = s2.getPoint((i + 1) % numPoints2);
			Vec2d edge = p2.minus(p1);
			Vec2d normal = new Vec2d(-edge.y, edge.x).normalize(); // Perpendicular to edge
			axes[axisIndex++] = normal;
		}

		double minOverlap = Double.MAX_VALUE;
		Vec2d mtvAxis = null;

		// 2. Project both polygons onto each axis and check for overlap
		for (Vec2d axis : axes) {
			// Project first polygon onto the axis
			double[] projection1 = projectPolygon(s1, axis);
			double min1 = projection1[0];
			double max1 = projection1[1];

			// Project second polygon onto the axis
			double[] projection2 = projectPolygon(s2, axis);
			double min2 = projection2[0];
			double max2 = projection2[1];

			// Calculate overlap
			double overlap = Math.min(max1, max2) - Math.max(min1, min2);
			if (overlap <= 0) {
				// No overlap means no collision
				return null;
			}

			// Track the smallest overlap for MTV
			if (overlap < minOverlap) {
				minOverlap = overlap;
				mtvAxis = axis;
			}
		}

		// 3. Adjust MTV direction to ensure it's pointing from s1 towards s2
		Vec2d s1Center = calculatePolygonCenter(s1);
		Vec2d s2Center = calculatePolygonCenter(s2);
		Vec2d direction = s2Center.minus(s1Center);

		// Check if mtvAxis is pointing in the same direction as the collision direction
		if (mtvAxis != null && mtvAxis.dot(direction) < 0) {
			mtvAxis = mtvAxis.smult(-1); // Reverse the MTV direction if it's pointing the wrong way
		}

		// 4. Return the MTV vector (minimum overlap along the correct direction)
		return mtvAxis.smult(-minOverlap);
	}

	// Helper method to calculate the approximate center of a polygon
	private Vec2d calculatePolygonCenter(PolygonShape polygon) {
		Vec2d center = new Vec2d(0, 0);
		for (int i = 0; i < polygon.getNumPoints(); i++) {
			center = center.plus(polygon.getPoint(i));
		}
		return center.smult(1.0 / polygon.getNumPoints());
	}

	// Helper method to project AABB onto an axis
	private double[] projectAABB(AABShape aabb, Vec2d axis) {
		Vec2d[] corners = new Vec2d[]{
				aabb.getTopLeft(),
				aabb.getTopLeft().plus(new Vec2d(aabb.getSize().x, 0)),
				aabb.getTopLeft().plus(aabb.getSize()),
				aabb.getTopLeft().plus(new Vec2d(0, aabb.getSize().y))
		};

		double min = corners[0].dot(axis);
		double max = min;
		for (int i = 1; i < corners.length; i++) {
			double projection = corners[i].dot(axis);
			min = Math.min(min, projection);
			max = Math.max(max, projection);
		}
		return new double[]{min, max};
	}

	// Helper method to project Polygon onto an axis
	private double[] projectPolygon(PolygonShape polygon, Vec2d axis) {
		double min = polygon.getPoint(0).dot(axis);
		double max = min;
		for (int i = 1; i < polygon.getNumPoints(); i++) {
			double projection = polygon.getPoint(i).dot(axis);
			min = Math.min(min, projection);
			max = Math.max(max, projection);
		}
		return new double[]{min, max};
	}

	// Helper method to project a circle onto an axis
	private double[] projectCircle(CircleShape circle, Vec2d axis) {
		Vec2d center = circle.getCenter();
		double radius = circle.getRadius();
		double centerProjection = center.dot(axis);
		return new double[]{centerProjection - radius, centerProjection + radius};
	}
	
	// RAYCASTING

	@Override
	public float raycast(AABShape s1, Ray s2) {
		Vec2d[] corners = {
				s1.getTopLeft(),
				s1.getTopLeft().plus(new Vec2d(s1.getSize().x, 0)),
				s1.getTopLeft().plus(s1.getSize()),
				s1.getTopLeft().plus(new Vec2d(0, s1.getSize().y))
		};

		float minT = Float.MAX_VALUE;
		boolean intersectionFound = false;

		// Iterate over each edge of the AABB (4 edges in total)
		for (int i = 0; i < 4; i++) {
			// Define the current edge with two vertices
			Vec2d a = corners[i];
			Vec2d b = corners[(i + 1) % 4];

			// Vectors from ray source to each endpoint of the edge
			Vec2d vecA = a.minus(s2.src);
			Vec2d vecB = b.minus(s2.src);

			// Cross product to check if segment straddles the ray
			float crossA = (float) vecA.cross(s2.dir);
			float crossB = (float) vecB.cross(s2.dir);

			// If cross products are both positive or both negative, skip this edge
			if (crossA * crossB > 0) {
				continue;
			}

			// Calculate the edge normal for the intersection equation
			Vec2d edgeDir = b.minus(a).normalize();
			Vec2d edgeNormal = new Vec2d(-edgeDir.y, edgeDir.x); // Perpendicular to the edge

			// Calculate t using the formula t = ( (b - p) • n ) / ( d • n )
			float numerator = (float) vecB.dot(edgeNormal);
			float denominator = (float) s2.dir.dot(edgeNormal);

			// If denominator is zero, the ray is parallel to the edge
			if (Math.abs(denominator) < 1e-6) {
				continue;
			}

			float t = numerator / denominator;

			// Only consider positive t values (in the direction of the ray)
			if (t >= 0) {
				intersectionFound = true;
				minT = Math.min(minT, t);
			}
		}

		// Return the smallest positive t value if any intersection is found
		return intersectionFound ? minT : -1;
	}


	@Override
	public float raycast(CircleShape s1, Ray s2) {
		Vec2d raySrctoCenter = s1.getCenter().minus(s2.src);
		Vec2d centerProjOnDir = raySrctoCenter.projectOnto(s2.dir);

		if (centerProjOnDir.dot(s2.dir) < 0) { // if behind, not real collisioin
			return -1;
		}

		Vec2d centerProjOnDirRay = s2.src.plus(centerProjOnDir);
		// Calculate the vector and squared distance from the closest point on the ray to the circle's center
		Vec2d projToCenter = s1.getCenter().minus(centerProjOnDirRay);
		float distanceToCenterSquared = (float) (projToCenter.x * projToCenter.x + projToCenter.y * projToCenter.y);

		// Check if the distance from the ray to the circle's center is greater than the radius (no intersection)
		float radiusSquared = s1.getRadius() * s1.getRadius();
		if (distanceToCenterSquared > radiusSquared) {
			return -1; // No intersection
		}

        // Calculate the distance along the ray to the intersection point
		float distanceToIntersection = (float) Math.sqrt(radiusSquared - distanceToCenterSquared);
		// Determine if the ray's origin is inside the circle
		float t;
		if (raySrctoCenter.mag() < s1.getRadius()) {
			// If the ray's origin is inside the circle, we add the distance to the intersection
			t = (float) (centerProjOnDir.mag() + distanceToIntersection);
		} else {
			// If the ray's origin is outside the circle, we subtract the distance to the intersection
			t = (float) (centerProjOnDir.mag() - distanceToIntersection);
		}

		// If t is negative, the intersection point is before the ray's origin
		if (t < 0) {
			return -1;
		}

		// Return the distance along the ray to the first intersection point
		return t;
	}

	@Override
	public float raycast(PolygonShape s1, Ray s2) {
		int numPoints = s1.getNumPoints();
		float minT = Float.MAX_VALUE;
		boolean intersectionFound = false;

		// Iterate over each edge of the polygon
		for (int i = 0; i < numPoints; i++) {
			// Define the current edge with two vertices
			Vec2d a = s1.getPoint(i);
			Vec2d b = s1.getPoint((i + 1) % numPoints); // Wrap around to the first point

			// Vectors from ray source to each endpoint of the edge
			Vec2d vecA = a.minus(s2.src);
			Vec2d vecB = b.minus(s2.src);

			// Cross product to check if segment straddles the ray
			float crossA = (float) vecA.cross(s2.dir);
			float crossB = (float) vecB.cross(s2.dir);

			// If cross products are both positive or both negative, skip this edge
			if (crossA * crossB > 0) {
				continue;
			}

			// Calculate the edge normal for the intersection equation
			Vec2d edgeDir = b.minus(a).normalize();
			Vec2d edgeNormal = new Vec2d(-edgeDir.y, edgeDir.x); // Perpendicular to the edge

			// Calculate t using the formula t = ( (a - p) • n ) / ( d • n )
			float numerator = (float) vecA.dot(edgeNormal);
			float denominator = (float) s2.dir.dot(edgeNormal);

			// If denominator is zero, the ray is parallel to the edge
			if (Math.abs(denominator) < 1e-6) {
				continue; // Skip this edge if parallel to the ray
			}

			float t = numerator / denominator;

			// Only consider positive t values (in the direction of the ray)
			if (t >= 0) {
				intersectionFound = true;
				minT = Math.min(minT, t);
			}
		}

		// Return the smallest positive t value if any intersection is found
		return intersectionFound ? minT : -1;
	}


}
