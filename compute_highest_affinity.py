#Main program test_compute_main.py
#This function finds the pait of webpages that has been viewed maximun number of times

def highest_affinity(site_list, user_list, time_list):
    def combinations(iterable, r):
        pool = tuple(iterable)
        n = len(pool)
        if r > n:
            return
        indices = range(r)
        yield tuple(pool[i] for i in indices)
        while True:
            for i in reversed(range(r)):
                if indices[i] != i + n - r:
                    break
            else:
                return
            indices[i] += 1
            for j in range(i+1, r):
                indices[j] = indices[j-1] + 1
            yield tuple(pool[i] for i in indices)

    site_list_set = set(site_list)
    user_list_set = set(user_list)
    time_list = [1238972321, 1238972456, 1238972618, 1238972899, 1248472489, 1258861829]

    site_pairs = []
    for subset in combinations(site_list_set,2):
        site_pairs.append(subset)

    user_index = 0
    names =[]
    per_user_index = []
    for person in user_list:
        names.append(person)
        per_user_index.append(user_index)
        user_index += 1

    name_site_dict = {}
    for ul in user_list_set:
        name_site_dict[ul] = []
    site_index = 0
    for name in names:
        if name in name_site_dict:
            name_site_dict[name].append(site_list[site_index])
        site_index += 1

    pair_site_dict = {}
    for sd in site_pairs:
        pair_site_dict[sd] = 0

    for k,v in name_site_dict.iteritems():
        for l in site_pairs:
            if (l[0] in v and l[1] in v):
                pair_site_dict[l] += 1

    sorted_x = sorted(pair_site_dict.items(), key= lambda x: x[1], reverse=True)
    highest_affinity_list =  sorted_x[0][0]
    highest_affinity = sorted(highest_affinity_list)

    return tuple(highest_affinity)





































