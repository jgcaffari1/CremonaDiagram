package graphVis;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.sound.*;

/**
 * class for generating a simple shpape for physics simulations.
 * 
 * @author jgcaf
 */
public class Mover {

  private final double G = 6.77;
  private final double k = 0.02;
  double radius = 15;
  private double diameter = 2 * radius;
  private double density = 5;
  private final Integer label;
  public boolean saved = false;
  public boolean pin = false;
  public boolean visited = false;
  int rd;
  int g;
  int b;
  Hashtable<Integer, SinOsc> sine = new Hashtable<>();

  Vector location;
  Vector velocity;
  Vector acceleration;
  private double mass;
  private PApplet p;
  private Random r;
  public double Cd = 0.004; // if this gets too high, then the spheres bounce off the surface.
                            // becomes
  // computationally unstable
  private double soundConstant = 100000 / 2.5;
  protected Integer distanceFromMouse = 0;
  double soundAmplitude = 1.0;
  boolean soundOn = true;

  public Mover(double[] d, double[] v, double[] a, double radius, double density, Integer key,
      PApplet processing) {
    label = key;
    location = new Vector(d[0], d[1], d[2]);
    velocity = new Vector(v[0], v[1], v[2]);
    acceleration = new Vector(a[0], a[1], a[2]);
    p = processing;
    this.density = density;
    this.radius = radius;
    this.diameter = 2 * this.radius;
    this.mass = (double) Math.PI * Math.pow(this.radius, 2) * density;

  }

  /**
   * initializes the mover with the key that its stored in a data structure.
   * 
   * @param key        - the graph key of this mover.
   * @param processing - the processing applet.
   */
  public Mover(Integer key, PApplet processing) {
    r = new Random();
    label = key;
    location = new Vector((r.nextInt(Visualizer.MAX_X - Visualizer.GUI_X) + Visualizer.GUI_X),
        r.nextInt(Visualizer.MAX_Y + 1));
    velocity = new Vector(0, 0, 0);
    acceleration = new Vector(0, 0, 0);
    mass = (double) Math.PI * Math.pow(radius, 2) * density;
    p = processing;
    setRed(255);
    setBlue(0);
    setGreen(127);

  }

  public Mover(PApplet processing, Vector target, Integer key, double radius) {
    label = key;
    location = new Vector(target.x, target.y);
    velocity = new Vector(0, 0, 0);
    acceleration = new Vector(0, 0, 0);
    mass = (double) Math.PI * Math.pow(radius, 2) * density;
    this.radius = radius;
    diameter = 2 * radius;
    p = processing;
  }

  public void createSound(PApplet processing, Integer key) {
    // Create the sine oscillator.
    // Create the noise generator
    SinOsc tmpSound = new SinOsc(processing);

    tmpSound.freq(0);
    tmpSound.amp((float) soundAmplitude);
    tmpSound.play();
    sine.put(key, tmpSound);
  }

  /**
   * adds sound to the out edges
   * 
   * @param k    - the edge weight/ spring constant
   * @param drag - the drag constant on the mover's velocity
   * @param key  - the id of the end point of the edge.
   */
  public void simpleHarmonicMotionFrequency(double k, double drag, Integer key,
      Vector velocityTarg) {
    if (!soundOn) {
      Vector relativeVelocity;
      // w = sqrt(k/m) = 2pif:
      // calculate the resonant frequency:
      double wo = Math.sqrt(k / mass);
      // calculate the damping ratio:
      double sigma = drag / (2 * Math.sqrt(k * mass));
      // determine the oscillation frequecy:
      double w = wo * Math.sqrt(1 - Math.pow(sigma, 2));
      double f = soundConstant * w / (2 * Math.PI);
      // check if the given edge already has a sound:
      if (sine.get(key) != null) {
        // create a sound amplitude that depends on the velocity
        if (!pin) {
          relativeVelocity = Vector.sub(velocityTarg, velocity);
          // reset the amplitude if the relative Velocity
          if (relativeVelocity.mag() > 1) {
            relativeVelocity.normalize();
          }
        } else {
          relativeVelocity = Vector.sub(velocityTarg, new Vector(0, 0));
        }
        // make the sound amplitude dependent on the relative veocities of the target.
        soundAmplitude = ((float) Math.exp(-sigma * wo)) * relativeVelocity.mag() / 2;
        SinOsc tmpSound = sine.remove(key);
        tmpSound.freq((float) f);
        tmpSound.amp((float) ((float) soundAmplitude));
        tmpSound.play();
        sine.put(key, tmpSound);
      } else {
        // otherwise make one:
        createSound(p, key);
      }
    } else {
      // stop sound if the sound is off:
      if (sine.get(key) != null) {
        sine.get(key).stop();
      }
    }

  }

  public void removeSoundsEndingAt(int endKey) {
    if (sine.get(endKey) == null) {
      return;
    }
    sine.get(endKey).stop();
    sine.remove(endKey);
  }

  public void removeSoundsFromEdgesStartingHere() {
    Enumeration<Integer> keys = sine.keys();
    Integer nextKey = null;
    while (keys.hasMoreElements()) {
      nextKey = keys.nextElement();
      sine.get(nextKey).stop();
      sine.remove(nextKey);

    }
  }

  /**
   * gets the label of this mover.
   * 
   * @return
   */
  public Integer getLabel() {
    return label;
  }

  /**
   * updates the kinematic state of this mover.
   */
  void update() {
    if (!pin) {
      velocity.add(acceleration);
      location.add(velocity);
    }
    // resetting acceleration to avoid jerk:
    acceleration.scale(0);

  }

  /**
   * displays this mover.
   */
  void display() {
    int red = rd;
    int blue = b;
    int green = g;

    if (pin) {
      red = 0;
      blue = 255;
    }
    if (visited) {
      red = 0;
      green = 255;
    }
    if (!mouseIsOver()) {
      p.strokeWeight(2);
      p.stroke(0);

    } else {
      p.strokeWeight(5);
      p.stroke(200);

    }
    if (!soundOn) {
      int edgeRed = (int) (127 * Math.sin(Math.sqrt(10 / mass)) + 127);
      p.stroke(edgeRed, 0, 0);
    }
    p.fill(red, green, blue);
    if (saved) {
      p.rectMode(PConstants.RADIUS);
      p.square((float) location.x, (float) location.y, (float) (radius));
    } else {
      p.circle((float) location.x, (float) location.y, (float) (2 * radius));
    }

  }

  public void setRed(int red) {
    rd = red;
  }

  public void setBlue(int blue) {
    b = blue;
  }

  public void setGreen(int green) {
    g = green;
  }

  /**
   * checks if a mover is outside of the window.
   * 
   * @return - true if out of bounds, false otherwise.
   */
  public boolean checkIfOutside() {
    boolean isOutsideWindow = false;
    if ((location.x > Visualizer.MAX_X - radius) || (location.x < Visualizer.GUI_X + radius)
        || (location.y > Visualizer.MAX_Y - radius) || (location.y < radius)) {
      isOutsideWindow = true;
    }
    return isOutsideWindow;
  }

  /**
   * allows the mover to bounce off of the boundaries with the certain damping constant.
   * 
   * @param damping - the damping constant from collisions.
   */
  public void bounce(double damping) {
    // Check for bouncing.
    if ((location.x >= Visualizer.MAX_X || (location.x <= Visualizer.GUI_X))) {
      location.x = (location.x > Visualizer.MAX_X) ? (Visualizer.MAX_X) : (Visualizer.GUI_X);
      velocity.x = velocity.x * -damping;
    }
    if ((location.y >= Visualizer.MAX_Y) || (location.y <= 0)) {
      location.y = (location.y > Visualizer.MAX_Y) ? (Visualizer.MAX_Y) : (0);
      velocity.y = velocity.y * -damping;
    }
  }

  public void rightClicked() {
    if (mouseIsOver() && p.mousePressed == true && p.mouseButton == PConstants.RIGHT) {
      this.pin = !this.pin;
    }
  }

  public void leftClicked() {
    if (mouseIsOver() && p.mousePressed == true && p.mouseButton == PConstants.LEFT) {
      this.saved = !this.saved;
    }
  }

  public boolean mouseIsOver() {
    Vector mouse;
    Vector rHat;

    mouse = new Vector(p.mouseX, p.mouseY);
    rHat = Vector.sub(mouse, location);
    // if mouse is over this element, then move it:
    if (rHat.mag() <= radius) {
      return true;
    }
    return false;
  }

  public boolean mouseNear(double distance) {
    Vector mouse;
    Vector rHat;

    mouse = new Vector(p.mouseX, p.mouseY);
    rHat = Vector.sub(mouse, location);
    // if mouse is over this element, then move it:
    if (rHat.mag() <= radius + distance) {
      return true;
    }
    return false;
  }

  /**
   * apply static friction and drag force.
   * 
   * @param kinetic - the coeficient of kinetic friction., stat
   */
  public void applyDragForce(double kinetic, double stat) {
    // apply friction:
    if (velocity.mag() == 0) {
      // resists current forces being applied if the velocity is not moving
      applyForce(Vector.mult(acceleration, -stat * mass));
    } else {
      // apply drag force:
      applyForce(Vector.mult(velocity, -kinetic));
    }
  }

  /**
   * limits the velocity to this specific maximum
   * 
   * @param max - the max velocity
   */
  public void limitVelocity(double max) {
    double V = velocity.mag();
    if (velocity.mag() > max) {
      velocity.x = (max * velocity.x / V);
      velocity.y = (max * velocity.y / V);
    }
  }

  /**
   * applies a force to this mover.
   * 
   * @param force - the force vector being applied.
   */
  public void applyForce(Vector force) {
    Vector a = Vector.div(force, mass);
    acceleration.add(a);
  }

  /**
   * gets the mass of the mover
   * 
   * @return - double representing the mass
   */
  public double getMass() {
    return mass;
  }

  /**
   * gets the diameter of the current mover
   * 
   * @return - the diameter
   */
  public double getDiameter() {
    return diameter;
  }

  /**
   * gets the radius of the current mover.
   * 
   * @return - the radius
   */
  public double getRadius() {
    return radius;
  }

  /**
   * exerts a gravity like force on all other objects.
   * 
   * @param m - the other mover.
   */
  public void pull(Mover m) {
    double m2 = m.getMass();
    Vector rHat = Vector.sub(location, m.location);
    double r = rHat.mag();
    // set the maximum force to be the force at the surface of each object:
    if (r <= radius) {
      r = radius;
    }
    rHat.normalize();
    double Fg = (G * mass * m2) / (Math.pow(r, 2));
    rHat.scale(Fg);
    m.applyForce(rHat);
  }

  /**
   * applies an electrostatic like repulsion between objects- like a reverse gravity.
   * 
   * @param m - the other mover.
   */
  public void push(Mover m) {
    if (m == null) {
      return;
    }
    double m2 = m.getMass();
    // want to apply force from current location towards the other object location.
    Vector rHat = Vector.sub(m.location, location);
    double r = rHat.mag();
    // set the maximum force to be the force at the surface of each object:
    if (r <= radius) {
      r = radius;
    }
    rHat.normalize();
    double Fe = (k * mass * m2) / (Math.pow(r, 2));
    rHat.scale(Fe);
    m.applyForce(rHat);
  }

  /**
   * applies a spring force based upon the distance between two movers and the connecting band
   * length.
   * 
   * @param m          - the mover at the other end of the spring
   * @param edgeWeight - the resting length of the spring.
   */
  public void springForce(Mover m, double edgeWeight, double damping) {
    Vector rHat = Vector.sub(location, m.location);
    double forceMag = edgeWeight * (rHat.mag() - 2 * (radius + m.radius));
    // set the sound the vertex plays as its oscillatory frequency:
    simpleHarmonicMotionFrequency(edgeWeight, damping, m.label, m.velocity);
    // normalize and scale the spring force:
    rHat.normalize();
    rHat.scale(forceMag);
    m.applyForce(rHat);

  }

  /**
   * applies a repellant force similar to the electrostatic force. edgeWegiht is the numerator of
   * the interaction.
   * 
   * @param m          - the mover at the other end of the force
   * @param edgeWeight - the numerator of force - edgeWeight/r^2
   */
  public void push(Mover m, double edgeWeight) {
    // want to apply force from current location towards the other object location.
    Vector rHat = Vector.sub(m.location, location);
    double r = rHat.mag();
    rHat.normalize();
    // if the objects are touching, then do not apply force.
    rHat.scale(edgeWeight / Math.pow(r, 2));
    m.applyForce(rHat);
  }

  public void pushSpring(Mover m, double mag) {
    // want to apply force from current location towards the other object location.
    Vector rHat = Vector.sub(m.location, location);
    rHat.normalize();
    // if the objects are touching, then do not apply force.
    rHat.scale(mag);
    m.applyForce(rHat);
  }

  public void drag(int mouseX, int mouseY) {
    location.x = mouseX;
    location.y = mouseY;
  }

  public boolean isColliding(Mover m) {
    if (m == null) {
      return false;
    }
    Vector rHat = Vector.sub(location, m.location);
    if (rHat.mag() <= m.radius + radius) {
      return true;
    } else {
      return false;
    }
  }

  public void collision(Mover m, float damping) {
    if (isColliding(m)) {
      // if the nodes are going opposite directions, then reflect them off of each other:
      if ((velocity.x < 0 && m.velocity.x > 0) || (velocity.x > 0 && m.velocity.x < 0)) {
        velocity.x = velocity.x * -damping;
        m.velocity.x = m.velocity.x * -damping;
      } else {
        // otherwise transfer momentum to the slower object:
        if (velocity.x < 0) {
          velocity.x -= m.velocity.x;
          m.velocity.x += velocity.x;
        } else {
          velocity.x += m.velocity.x;
          m.velocity.x -= velocity.x;
        }
      }
      // if the nodes are going opposite directions, then reflect them off of each other:
      if ((velocity.y < 0 && m.velocity.y > 0) || (velocity.y > 0 && m.velocity.y < 0)) {
        velocity.y = velocity.y * -1;
        m.velocity.y = m.velocity.y * -1;
      } else {
        // otherwise transfer momentum to the slower object:
        if (velocity.y < 0) {
          velocity.y -= m.velocity.y;
          m.velocity.y += velocity.y;
        } else {
          velocity.y += m.velocity.y;
          m.velocity.y -= velocity.y;
        }
      }
    }
  }

  /**
   * brownian motion walk towards the mouse position.
   */
  public void moveTowardsMouse() {
    int mX = p.mouseX;
    int mY = p.mouseY;
    int stepX = r.nextInt(5) - 1;
    int stepY = r.nextInt(5) - 1;
    if (location.x < mX) {
      location.x += stepX;
    } else if (location.x > mX) {
      location.x -= stepX;
    }
    if (location.y < mY) {
      location.y += stepY;

    } else if (location.y > mY) {
      location.y -= stepY;
    }

  }

  public void moveInBounds() {
    if (location.x < Visualizer.GUI_X + radius) {
      location.x = Visualizer.GUI_X + radius;
    } else if (location.x > Visualizer.MAX_X - radius) {
      location.x = Visualizer.MAX_X - radius;
    }
    if (location.y < 0 + radius) {
      location.y = 0 + radius;
    } else if (location.y > Visualizer.MAX_Y - radius) {
      location.y = Visualizer.MAX_Y - radius;
    }
  }

  public boolean equals(Mover m) {
    return this.label == m.label;
  }

  /**
   * compares this vertex to another vertex based on key.
   * 
   * @param key - the key of the other vertex.
   * @return -1 if the other vertex is less than this node, 0 if equal, and 1 if greater than.
   */
  public int compareTo(Mover m) {
    return this.distanceFromMouse.compareTo(m.distanceFromMouse);
  }

  public void updateDistance(int mouseX, int mouseY) {
    Vector mouse = new Vector(mouseX, mouseY);
    Vector rHat = Vector.sub(mouse, location);
    double r = rHat.mag();
    this.distanceFromMouse = (int) r;
  }

  public String toString() {
    return Integer.toString(this.label);
  }

}
