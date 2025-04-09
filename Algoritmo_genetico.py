import random
import copy
import time  # Importamos para usar o time.sleep

class Cromossomo:
    def __init__(self, rota):
        self.rota = rota
        self.penalidade = self.calcular_penalidade()
    
    def calcular_penalidade(self):
        penalidade = 0
        
        # 1. Penalidade por cidade maior antes de menor
        for i in range(len(self.rota)):
            for j in range(i+1, len(self.rota)):
                if int(self.rota[i]) > int(self.rota[j]):
                    penalidade += 10
        
        # 2. Penalidade por cidades repetidas
        if len(set(self.rota)) != len(self.rota):
            penalidade += 20 * (len(self.rota) - len(set(self.rota)))
        
        return penalidade
    
    def __lt__(self, other):
        return self.penalidade < other.penalidade
    
    def __str__(self):
        return f"{self.rota} (Penalidade: {self.penalidade})"

def gerar_rota_aleatoria():
    cidades = list("123456789")
    random.shuffle(cidades)
    return ''.join(cidades)

def exibir(populacao):        
    for i in populacao:
        print(i)
        time.sleep(0.05)  # Pequeno delay entre cada indivíduo

def main():
    print("=== Algoritmo Genético para Rota de Cidades ===")
    print("Objetivo: Encontrar a rota 1-2-3-4-5-6-7-8-9 com penalidade 0\n")
    
    # Configuração
    tamanho_populacao = int(input("Tamanho da população: "))
    taxa_mutacao = int(input("Taxa de mutação (%): "))
    max_geracoes = int(input("Máximo de gerações: "))
    
    # População inicial
    populacao = [Cromossomo(gerar_rota_aleatoria()) for _ in range(tamanho_populacao)]
    populacao.sort()
    
    print("\n=== Geração 1 ===")
    exibir(populacao)
    
    for geracao in range(1, max_geracoes):
        nova_populacao = []
        
        # Elitismo (mantém o melhor)
        nova_populacao.append(populacao[0])
        
        # Seleção por torneio
        while len(nova_populacao) < tamanho_populacao:
            torneio = random.sample(populacao, 3)
            vencedor = min(torneio)
            nova_populacao.append(vencedor)
        
        # Crossover
        filhos = []
        for _ in range(tamanho_populacao // 2):
            pai, mae = random.sample(nova_populacao, 2)
            
            # Crossover OX
            corte1, corte2 = sorted(random.sample(range(9), 2))
            filho1 = [''] * 9
            filho1[corte1:corte2] = pai.rota[corte1:corte2]
            
            pos_mae = 0
            for i in range(9):
                if filho1[i] == '':
                    while mae.rota[pos_mae] in filho1:
                        pos_mae += 1
                    filho1[i] = mae.rota[pos_mae]
            
            filhos.append(Cromossomo(''.join(filho1)))
            
            # Segundo filho (invertendo pais)
            filho2 = [''] * 9
            filho2[corte1:corte2] = mae.rota[corte1:corte2]
            
            pos_pai = 0
            for i in range(9):
                if filho2[i] == '':
                    while pai.rota[pos_pai] in filho2:
                        pos_pai += 1
                    filho2[i] = pai.rota[pos_pai]
            
            filhos.append(Cromossomo(''.join(filho2)))
        
        # Mutação
        for i in range(len(filhos)):
            if random.random() < taxa_mutacao / 100:
                idx1, idx2 = random.sample(range(9), 2)
                rota_mutada = list(filhos[i].rota)
                rota_mutada[idx1], rota_mutada[idx2] = rota_mutada[idx2], rota_mutada[idx1]
                filhos[i] = Cromossomo(''.join(rota_mutada))
        
        # Nova população
        populacao = copy.deepcopy(filhos)
        populacao.sort()
        
        print(f"\n=== Geração {geracao + 1} ===")
        exibir(populacao)
        
        # Critério de parada
        if populacao[0].penalidade == 0:
            print("\n✅ Solução perfeita encontrada!")
            break
    
    print("\n=== Resultado Final ===")
    print(f"Melhor rota encontrada: {populacao[0]}")
    print(f"Total de gerações: {geracao + 1}")

if __name__ == "__main__":
    main()