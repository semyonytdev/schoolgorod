import gzip, os, struct, sys

def raw_name(n):
    b=n.encode("utf-8")
    return struct.pack(">H",len(b))+b
def tag(t,n,p):
    return bytes([t])+raw_name(n)+p
def string(v):
    b=v.encode("utf-8")
    return struct.pack(">H",len(b))+b
def integer(v): return struct.pack(">i",v)
def compound(items): return b"".join(items)+b"\x00"
def st(n,v): return tag(8,n,string(v))
def it(n,v): return tag(3,n,integer(v))
def ia(n,values): return tag(11,n,integer(len(values))+b"".join(integer(v) for v in values))
def doubles(n,values): return tag(6,n,integer(len(values))+b"".join(struct.pack(">d",v) for v in values))
def floats(n,values): return tag(5,n,integer(len(values))+b"".join(struct.pack(">f",v) for v in values))
def ct(n,items): return tag(10,n,compound(items))
def lt(n,t,items): return tag(9,n,bytes([t])+integer(len(items))+b"".join(items))

W,H,D=25,30,25
block_names=[
"minecraft:air","minecraft:deepslate_bricks","minecraft:polished_blackstone_bricks",
"minecraft:dark_oak_planks","minecraft:glass","minecraft:lantern","minecraft:chest",
"minecraft:stone_bricks","minecraft:bookshelf","minecraft:lectern","minecraft:sculk",
"minecraft:polished_deepslate","minecraft:ladder","minecraft:jigsaw","minecraft:iron_bars",
"minecraft:oak_stairs","minecraft:polished_blackstone","schoolgorod:pink_shrieker"
]
palette=[compound([st("Name",n)]) for n in block_names]
def make(include_nuze):
    blocks={}
    def put(x,y,z,state,loot=None):
        if 0<=x<W and 0<=y<H and 0<=z<D:
            blocks[(x,y,z)]=(state,loot)
    def fill(x1,y1,z1,x2,y2,z2,state):
        for y in range(y1,y2+1):
            for z in range(z1,z2+1):
                for x in range(x1,x2+1): put(x,y,z,state)
    # Outer shell and three storeys
    for fy in (6,13,20,29): fill(0,fy,0,24,fy,24,1)
    for y in range(1,29):
        for x in range(25): put(x,y,0,2); put(x,y,24,2)
        for z in range(25): put(0,y,z,2); put(24,y,z,2)
    for fy in (7,14,21):
        for y in range(fy,fy+6):
            for x in (5,6,7,17,18,19): put(x,y,0,4); put(x,y,24,4)
            for z in (5,6,7,17,18,19): put(0,y,z,4); put(24,y,z,4)
        fill(2,fy,11,22,fy+5,13,3)
        for z in (4,9,15,20):
            for y in range(fy,fy+6):
                for x in range(2,23):
                    if x not in (11,12,13): put(x,y,z,7)
        for x,z in ((5,6),(18,6),(5,18),(18,18)):
            put(x,fy+1,z,8); put(x,fy+1,z+1,9); put(x,fy+3,z,5)
        put(5,fy,5,6,"schoolgorod:chests/classroom")
        put(19,fy,5,6,"schoolgorod:chests/locker")
    # Vertical ladder shaft linking all floors
    for y in range(1,29):
        put(12,y,10,12); put(12,y,14,12)
        put(11,y,12,14); put(13,y,12,14)
    # NUZE washroom with stalls; rare 2% template variant gets the stash chest
    fill(2,14,2,9,14,8,7)
    for x in (4,6,8):
        for z in range(3,8): put(x,15,z,14)
        put(x,15,4,0); put(x,15,7,0)
    if include_nuze:
        put(8,15,7,6,"schoolgorod:chests/bathroom_stash")
    # Underground bunker and Director arena
    fill(3,1,3,21,5,21,0)
    for x in range(3,22): put(x,1,3,2); put(x,1,21,2)
    for z in range(3,22): put(3,1,z,2); put(21,1,z,2)
    put(12,2,12,6,"schoolgorod:chests/director_cache")
    # Top-floor abandoned classroom
    fill(3,19,3,9,21,9,0)
    for x in range(3,10): put(x,19,3,7); put(x,19,9,7)
    for z in range(3,10): put(3,19,z,7); put(9,19,z,7)
    put(8,20,8,6,"schoolgorod:chests/abandoned_class")
    put(20,15,5,6,"schoolgorod:chests/director_cache")
    # Jigsaw anchor for the structure start pool
    put(12,29,12,13)
    block_entries=[]
    for (x,y,z),(state,loot) in blocks.items():
        fields=[ia("pos",[x,y,z]),it("state",state)]
        if loot:
            fields.append(ct("nbt",[st("id","minecraft:chest"),st("LootTable",loot),tag(4,"LootTableSeed",struct.pack(">q",0))]))
        if state==13:
            fields.append(ct("nbt",[st("id","minecraft:jigsaw"),st("name","schoolgorod:anchor"),st("target","schoolgorod:anchor"),st("pool","minecraft:empty"),st("final_state","minecraft:air"),st("joint","rollable")]))
        block_entries.append(compound(fields))
    # Place the Director in the lower arena and two hostile students on each storey.
    def make_entity(entity_id, x, y, z, yaw=0.0):
        return compound([
            lt("pos",6,[struct.pack(">d",x),struct.pack(">d",y),struct.pack(">d",z)]),
            ia("blockPos",[int(x),int(y),int(z)]),
            ct("nbt",[
                st("id",entity_id),
                lt("Pos",6,[struct.pack(">d",x),struct.pack(">d",y),struct.pack(">d",z)]),
                lt("Rotation",5,[struct.pack(">f",yaw),struct.pack(">f",0.0)])
            ])
        ])
    entities=[make_entity("schoolgorod:director",12.5,2.0,12.5)]
    for fy in (7,14,21):
        entities.append(make_entity("schoolgorod:student",8.5,fy+1.0,7.5,90.0))
        entities.append(make_entity("schoolgorod:student",16.5,fy+1.0,17.5,-90.0))
    root=compound([
        it("DataVersion",4671),
        ia("size",[W,H,D]),
        lt("palette",10,palette),
        lt("blocks",10,block_entries),
        lt("entities",10,entities)
    ])
    return gzip.compress(tag(10,"",root),compresslevel=9)
for suffix,enabled in (("building",False),("building_nuze",True)):
    out=f"build/generated/schoolgorod/data/schoolgorod/structures/school/{suffix}.nbt"
    os.makedirs(os.path.dirname(out),exist_ok=True)
    payload=make(enabled)
    with open(out,"wb") as f: f.write(payload)
    print(f"{suffix}: {len(payload)} bytes")
