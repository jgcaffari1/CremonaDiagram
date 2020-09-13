/**
 *  A zero gravity Cremona Diagram created using processing and ControlIP5. 
 *  
 *  I ask that you cite / reference my github repo if you use this code as a reference.   
 *  
 *  Copyright (C) 2020  Joe Caffarini jgcaffari1@gmail.com
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 *  
 *  See the GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *  
 */


/**
 * This was created as an exercise from the Nature of Code: https://creativecommons.org/licenses/by-nc/3.0/
 */

package graphVis;

/**
 * vector class based off of the nature of code. It allows for vector operations
 * to be performed. It is essentially the same as the PVector class from
 * processing.
 */
public class Vector {

	double x;
	double y;
	double z;

	/**
	 * creates a 2d vector
	 * 
	 * @param x_ - x component
	 * @param y_ - y component
	 */
	public Vector(double x_, double y_) {
		x = x_;
		y = y_;
		z = 0.0;

	}

	/**
	 * creates a 3d vector
	 * 
	 * @param x_ - x component
	 * @param y_ - y component
	 * @param z_ - z component
	 */
	public Vector(double x_, double y_, double z_) {
		x = x_;
		y = y_;
		z = z_;
	}

	/**
	 * adds another vector to this vector
	 * 
	 * @param v - the other vector
	 */
	public void add(Vector v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}

	/**
	 * subtracts another vector from this vector.
	 * 
	 * @param v
	 */
	public void sub(Vector v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
	}

	/**
	 * takes the dot product with a given vector
	 * 
	 * @param v - the given vector.
	 * @return - the dot product of this vector and V
	 */
	public double dot(Vector v) {
		return x * v.x + y * v.y + z * v.z;
	}

	/**
	 * scales the amplitude of this vector by n
	 * 
	 * @param n - scaling factor
	 */
	public void scale(double n) {
		x *= n;
		y *= n;
		z *= n;
	}

	/**
	 * takes the cross product between this vector and V
	 * 
	 * @param v - the other vector.
	 */
	public void cross(Vector v) {
		z = x * v.y - y * v.x;
	}

	/**
	 * calculates the magnitude of this vector.
	 * 
	 * @return - the magnitude of this vector.
	 */
	public double mag() {
		return Math.sqrt((x * x) + (y * y) + (z * z));
	}

	/**
	 * normalizes this vector. It changes the actual vector itself.
	 */
	public void normalize() {
		double V = this.mag();
		if (V != 0.0) {
			this.div(V);
		} else {
			x = 0.0;
			y = 0.0;
			z = 0.0;
		}
	}

	/**
	 * adds the two vectors v and w
	 * 
	 * @param v - first vector
	 * @param w - second vector
	 * @return - a new vector that is v+w
	 */
	static Vector add(Vector v, Vector w) {
		return new Vector(v.x + w.x, v.y + w.y, v.z + w.z);
	}

	/**
	 * subtracts w from b
	 * 
	 * @param v - first vector
	 * @param w - second vector
	 * @return - a new vector that is v-w
	 */
	static Vector sub(Vector v, Vector w) {
		return new Vector(v.x - w.x, v.y - w.y, v.z - w.z);
	}

	/**
	 * divides each vector component by num
	 * 
	 * @param num - the number this vector is being divided by.
	 */
	public void div(double num) {
		x /= num;
		y /= num;
		z /= num;

	}

	/**
	 * normalizes the vector v
	 * 
	 * @param v - the vector being normalized
	 * @return - a new, normalized vector in the same direction of v.
	 */
	static Vector Normalize(Vector v) {
		double V = v.mag();
		Vector unitNorm;
		if (V != 0) {
			unitNorm = new Vector(v.x / V, v.y / V, v.z / V);
		} else {
			unitNorm = new Vector(0.0, 0.0, 0.0);
		}
		return unitNorm;
	}

	/**
	 * calculates the dot product between v and w
	 * 
	 * @param v - first vector
	 * @param w - second vector
	 * @return - the dot product.
	 */
	static double Dot(Vector v, Vector w) {
		return v.x * w.x + v.y * w.y + v.z * w.z;
	}

	static double cross(Vector u, Vector v) {
		return u.x * v.y - u.y * v.x;
	}

	/**
	 * divides the given vector by num
	 * 
	 * @param v   - the vector being divided.
	 * @param num
	 * @return - the rescaled vector.
	 */
	static Vector div(Vector v, double num) {
		return new Vector(v.x / num, v.y / num, v.z / num);
	}

	/**
	 * Multiplies the given vector by num
	 * 
	 * @param v   - the vector being multiplied.
	 * @param num
	 * @return the rescaled vector
	 */
	static Vector mult(Vector v, double num) {
		return new Vector(v.x * num, v.y * num, v.z * num);
	}

	public String toString() {
		return "(" + Double.toString(x) + "," + Double.toString(y) + "," + Double.toString(z) + ")";
	}

	/**
	 * copies the current vector
	 * 
	 * @return a new vector identical to this one.
	 */
	public Vector copy() {
		return new Vector(this.x, this.y, this.z);
	}

	/**
	 * returns the angle of this vector.
	 * 
	 * @return the angle of this vector wrt the positive x axis.
	 */
	public double heading() {
		return Math.atan2(y, x);
	}

}
