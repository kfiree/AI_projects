import numpy as np
from Adaline import Adaline
from Adaline import Part
import matplotlib.pyplot as plt

DATA_SIZE = 1000

def paintRed(msg): return "\033[91m {}\033[00m".format(msg)
def paintGreen(msg): return "\033[92m {}\033[00m".format(msg)

def generateSamples(data_size=DATA_SIZE, debug=False, part=Part.A):
    np.random.seed(1)
    x_int = np.random.randint(-100, 99, size=(data_size, 2))
    if part == Part.B:
        i = np.random.randint(0, 199)
        x_int[i][0] = 1
        x_int[i][1] = 1

    x_frac = np.round(np.random.rand(data_size, 2), 2)
    x = x_int + x_frac

    y = np.empty(shape=[data_size])
    if part == Part.A:
        for i in range(data_size):
            y[i] = 1 if x[i][1] > 1 else -1

    elif part == Part.B:
        X1 = x[:, 0] ** 2
        X2 = x[:, 1] ** 2
        sum = np.add(X1, X2)
        for i in range(data_size):
            y[i] = 1 if sum[i] >= 4 and sum[i] <= 9 else -1

    if debug:
        print("x: \n", x, "\n")
        print("y: \n", y, "\n")
        # print("data: \n", data, "\n")

    return x, y


def calculateResults(_x, _y, debug=False, part=Part.A):
    model = Adaline(part=part).train(_x, _y, debug=debug)
    false_negative, false_positive, true_negative, true_positive = model.test(_x, _y)

    print("                PART ", part.name,
          "\n               --------")
    if debug:
        print("weights: ", model.weights)

    # Confusion Matrix

    print("\n           Confusion Matrix:",
          "\n         ===================\n",
          paintGreen("True positive:"), true_positive, paintRed("False positive:"), false_positive,"\n",
          paintRed("False_negative:"), false_negative, paintGreen("True negative:"), true_negative)

    missed_class = false_positive + false_negative
    score = (DATA_SIZE - missed_class) / DATA_SIZE
    print("\n         \033[1mAccuracy:\033[0m ", score * 100, "% success")

    return model



def showResults(_x, _y, p=Part.A):
    model = calculateResults(_x, _y, part=p, debug=False)
    b = -model.weights[0] / model.weights[2]
    m = -model.weights[1] / model.weights[2]

    x = [-100, 100]
    y = [-100 * m + b, 100 * m + b]
    plt.plot(x, y)
    neg_x, neg_y, pos_x, pos_y = model.splitAsShouldBe(_x, _y)
    plt.scatter(pos_x, pos_y, label="stars", color="green", marker="*", s=30)
    plt.scatter(neg_x, neg_y, label="stars", color="red", marker="*", s=30)
    plt.xlabel('x - axis')
    plt.ylabel('y - axis')

    title = "PART " + p.name + ": Adaline Algorithm\n" + str(model.data_size) + " samples " + str(
        model.learning_rate) + " learning rate"
    plt.title(title)


    plt.xlim(_x[:, 0].min(), _x[:, 0].max())
    plt.ylim(_x[:, 1].min(), _x[:, 1].max())
    plt.show()

    plt.plot(model.costs, '-b', label="loss")
    plt.xlabel("n epoch")
    plt.legend(loc='upper left')
    title = "PART " + p.name + ": Loss progrees\n" + str(model.data_size) + " samples " + str(
        model.learning_rate) + " learning rate"
    plt.title(title)

    print("final weights: ",model.weights)


    # plt.plot(model.costs)
    plt.show()

def run(part):
    part = part
    _x, _y = generateSamples(debug=False, part=part)
    showResults(_x, _y, part)


run(Part.A)
run(Part.B)







def prYellow(msg): print("\033[93m {}\033[00m".format(msg))


def prLightPurple(msg): print("\033[94m {}\033[00m".format(msg))


def prPurple(msg): print("\033[95m {}\033[00m".format(msg))


def prCyan(msg): print("\033[96m {}\033[00m".format(msg))


def prLightGray(msg): print("\033[97m {}\033[00m".format(msg))


def prBlack(msg): print("\033[98m {}\033[00m".format(msg))
