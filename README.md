Space simulation in java written as part of my "Introduction to Programming 2" course. The main idea is to use an octree datastructure to speed-up the normally O(n^2) mutual attraction calculations to a linear-logarithmic factor. An interesting feature of my implementation is that I use [Morton code](https://en.wikipedia.org/wiki/Z-order_curve) to simplify the insertions into the tree.

![Screenshot](screenshot.png)
