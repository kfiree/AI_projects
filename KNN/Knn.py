import numpy as np
import matplotlib.pyplot as plt

ClassA, ClassB = 1, -1


class Knn(object):
    def __init__(self, data):
        self._data = data
        # data = self.split_samples_and_labels(data)
        # self._data = data[0]
        # self._labels = data[1]
        self._k = 1
        self._p = 1

    # def train(self, data):
    #     data = self.split_samples_and_labels(data)
    #     self._data = data[0]
    #     self._labels = data[1]

    def prediction(self, x_test, k, p):
        self._k = k
        self._p = p
        return np.apply_along_axis(func1d=self.predict_sample, axis=1, arr=x_test)

    def predict_sample(self, sample):
        return ClassA if sum(self.k_neighborhood(sample)[:, 2]) > 0 else ClassB

    def k_neighborhood(self, sample):
        """ init neighborhood list of k nearest samples, [distances, indexes] """
        samples = np.apply_along_axis(func1d=self.euclidean_distance, axis=1, arr=self._data, v2=sample)

        k_neighborhood = samples[:self._k]  # np.column_stack((distances[:self._k], np.array(list(range(self._k)))))
        k_neighborhood = k_neighborhood[k_neighborhood[:, 3].argsort()[::-1]]

        """ find k nearest vectors """
        for sample_i in samples[self._k:]:
            sample_dis = sample_i[3]
            k_neighbor_dis = k_neighborhood[0][3]

            if sample_dis < k_neighbor_dis:
                k_neighborhood[0] = sample_i

                k_neighborhood = k_neighborhood[k_neighborhood[:, 3].argsort()[::-1]]

        return k_neighborhood

    def split_samples_and_labels(self, data=None):
        data = self._data if data is None else data

        temp = np.hsplit(data, np.array([2, data.shape[0]]))
        return temp[0], temp[1]

    def euclidean_distance(self, v1, v2):
        return np.append(v1, np.linalg.norm(v1[:2] - v2, ord=self._p))

