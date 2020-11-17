# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer:
My implementation differed in that originally I tried doing all the math in the Hexagon class,
and was getting a headache trying to get the calculations straight rather than generalizing it
as we did at the end of the lab. I think this tells me to make things less complex by spreading
out the work over a number of classes and functions.
-----

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer:
In a sense, these hexagons are each kind of like a room. In project 3, we would be randomly generating these rooms
in a tessellation, which would reflect the world more generally as a combination of all these hexagons.
-----
**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer:
The first thing I would do would be to make a Room (Room.java) by defining it by its location, shape,
and aesthetic (TETile). Then I would start to build up from there by introducing tessellation by combining
them all together.
-----
**What distinguishes a hallway from a room? How are they similar?**

Answer:
The way I see it, rooms are somewhat distinct bodies that we can randomly generate, and hallways are 
generated as a consequence of room generation with the purpose of connecting rooms. They are both
similar in that there should not be gaps for either of them and they should have the same floor tile.
