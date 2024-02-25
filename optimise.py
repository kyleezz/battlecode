
import os
from subprocess import PIPE, Popen
import subprocess
import time
import random

# temp = subprocess.check_output(['./gradlew', 'listPlayers'])
# temp = temp.split()
# teams = [i.decode("utf-8") for i in temp[3:-9]]

teams = ["optimiseone", "optimisetwo"]
maps = ['Rivers', 'MazeRunner', 'Hockey', 'BigDucksBigPond', 'Ambush', 'AceOfSpades', 'DefaultHuge']


aConstants = [400, 16, 400, 3, 3, 2, 10]
bConstants = [600, 18, 600, 4, 6, 3, 11]
cConstants = []
vars = ["LOW_HEALTH_RETREAT", "RETREAT_ENEMY_RADIUS", "IDEAL_HEALTH", "MIN_TEAM_SIZE", "ESCORT_SIZE", "ENEMY_TURN_DIFF", "KITE_DISTANCE"]

lowerBound = [100, 10, 100, 1, 1, 0, 4]
upperBound = [800, 20, 800, 15, 8, 4, 16]
step = [100, 2, 100, 1, 1, 1, 1]

iteration = 1

def changeConstants():
    global cConstants
    cConstants = []
    for i in range(0, len(bConstants)):
        temp = bConstants[i] + random.randint(-1, 2) * step[i]
        if temp >= lowerBound[i] and temp <= upperBound[i]:
            cConstants.append(temp)
        else:
            cConstants.append(bConstants[i])

def writeFile(constants, id):
    with open("./src/{}/Optimise.java".format(id), "w") as f:
        f.write("package {};\n\n".format(id))
        f.write("public class Optimise {\n\n")
        for i in range(0, len(vars)):
            f.write("static int {} = {};\n".format(vars[i], str(constants[i])))
        f.write("\n}")

writeFile(aConstants, "optimiseone")
writeFile(bConstants, "optimisetwo")

while True:
    try:
        print("Current iteration: {}".format(iteration))
        iteration += 1

        changeConstants()
        writeFile(cConstants, "optimisethree")

        toadd = []

        for map in maps:
            toadd.append(("optimiseone", "optimisethree", map))
            toadd.append(("optimisethree", "optimiseone", map))
            toadd.append(("optimisetwo", "optimisethree", map))
            toadd.append(("optimisethree", "optimisetwo", map))

        print(toadd)
        nextTask = 0
        maxProcesses = 4
        maxTasks = len(toadd)
        processes = []

        threevsone = 0
        threevstwo = 0

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
            global threevsone
            global threevstwo

            for p in range(len(processes)-1,-1,-1):
                if processes[p].poll() is not None:
                    output = processes[p].communicate()[0].decode('utf-8')
                    output = output.split()
                    winIndex = output.index('wins')

                    winner = output[winIndex - 2]
                    info = processes[p].info

                    if info[0] == "optimiseone" or info[1] == "optimiseone":
                        if winner == "optimisethree":
                            threevsone += 1
                    else:
                        if winner == "optimisethree":
                            threevstwo += 1


                    # print(output.count(processes[p].info[0]))
                    # print(output.count(processes[p].info[1]))

                    del processes[p]

            while (len(processes) < maxProcesses) and (nextTask < maxTasks):
                StartNew()


        CheckRunning()
        while (len(processes) > 0):
            time.sleep(2)
            CheckRunning()

        print("STATS:")
        print(threevsone, threevstwo)

        if threevsone > len(maps) + 2 and threevstwo > len(maps) + 2:
            print("THIS IS BETTER")
            # set one to prevconstants
            # set two to currentconstants

            writeFile(bConstants, "optimiseone")
            writeFile(cConstants, "optimisetwo")

            aConstants = bConstants
            bConstants = cConstants
    except KeyboardInterrupt:
        break
    except:
        print("something crashed :(")