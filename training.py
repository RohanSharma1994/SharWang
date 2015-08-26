# This script is for generating the optimal weights for our evaluation function.

def Eval(weights, data): return sum([weights[i] * data[i] for i in range(len(weights))])

def label(i): return 1 if i == 1 else (0 if i == 2 else -1)

d = dict()
for i in range(16, 31):
    file = open("./data/data_%d" % i, "r")
    dataset = [map(int, line.strip('\n').split()) for line in file.readlines()]
    w1 = w2 = w3 = 1  # Initialize w1, w2 and w3
    # Optimize weights by gradient decent algorithm
    for data in dataset:
        z = Eval([w1, w2, w3], data[:3])
        t = label(data[-1])
        w1 = w1 - 0.001 * (z - t) * data[0]
        w2 = w2 - 0.001 * (z - t) * data[1]
        w3 = w3 - 0.001 * (z - t) * data[2]
    d[i] = [w1, w2, w3]

# Print all weights
for v in d.values():
    print "{%.6f, %.6f, %.6f}," % (v[0], v[1], v[2])