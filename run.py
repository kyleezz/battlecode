
import os
from subprocess import PIPE, Popen
import subprocess
import time

# temp = subprocess.check_output(['./gradlew', 'listPlayers'])
# temp = temp.split()
# teams = [i.decode("utf-8") for i in temp[3:-9]]

teams = ["boomerbomb", "zoomer"]
maps = ['AceOfSpades', 'Alien', 'Ambush', 'Battlecode24', 'BedWars', 'BigDucksBigPond', 'Bunkers', 'CH3353C4K3F4CT0RY', 'Canals', 'Checkered', 'DefaultHuge', 'DefaultLarge', 'DefaultMedium', 'DefaultSmall', 'Diagonal', 'Divergent', 'Duck', 'EndAround', 'FloodGates', 'Fountain', 'Foxes', 'Fusbol', 'GaltonBoard', 'HeMustBeFreed', 'Hockey', 'HungerGames', 'Intercontinental', 'Klein', 'MazeRunner', 'QueenOfHearts', 'QuestionableChess', 'Racetrack', 'Rainbow', 'Rivers', 'Snake', 'Soccer', 'SteamboatMickey']

results = [[0]*len(teams) for i in range(len(teams))]

print(results)
tonum = dict()

for idx, cur in enumerate(teams):
    tonum[cur] = idx

maxl = 0

toadd = []

for i in range(len(teams)):
    for j in range(len(teams)):
        if i <= j:
            continue
        for map in maps:
            toadd.append((teams[i], teams[j], map))
            toadd.append((teams[j], teams[i], map))
        # p = subprocess.Popen(['./gradlew', 'run', '-Pmaps=DefaultSmall', '-PteamA=farmybomb', '-PteamB=farmy'])
        # processes.append(p)
    # print("fml")
    # break

print(toadd)
nextTask = 0
maxProcesses = 8
maxTasks = len(toadd)
processes = []

def StartNew():
    global nextTask
    global maxTasks

    if nextTask < maxTasks:
        proc = Popen(['./gradlew', 'run', '-Pmaps=' + toadd[nextTask][2], '-PteamA=' + toadd[nextTask][0], '-PteamB=' + toadd[nextTask][1]], stdout=PIPE)
        proc.info = toadd[nextTask]
        print("{}/{}: Started Match {} vs. {} on map {}".format(nextTask + 1, maxTasks, toadd[nextTask][0], toadd[nextTask][1], toadd[nextTask][2]))
        nextTask += 1
        processes.append(proc)

def CheckRunning():
    global processes
    global nextTask

    for p in range(len(processes)-1,-1,-1):
        if processes[p].poll() is not None:
            output = processes[p].communicate()[0].decode('utf-8')
            output = output.split()
            winIndex = output.index('wins')

            winner = output[winIndex - 2]
            info = processes[p].info

            if winner == info[0]:
                results[tonum[info[0]]][tonum[info[1]]] += 1
            else:
                results[tonum[info[1]]][tonum[info[0]]] += 1


            # print(output.count(processes[p].info[0]))
            # print(output.count(processes[p].info[1]))

            del processes[p]

    while (len(processes) < maxProcesses) and (nextTask < maxTasks):
        StartNew()


CheckRunning()
while (len(processes) > 0):
    time.sleep(2)
    CheckRunning()

for i in range(len(teams)):
    print(teams[i].center(60, "-"))
    win = 0
    for x in results[i]:
        win += x
    total = (len(teams) - 1) * len(maps) * 2
    print("{}/{}".format(win, total))
    print("Win Rate: " + str(float(win/total) * 100) + "%")

    for j in range(len(teams)):
        if i == j:
            continue
        print("{} vs. {}: {} - {}".format(teams[i], teams[j], results[i][j], results[j][i]))