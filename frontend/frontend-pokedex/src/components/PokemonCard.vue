<template>
  <div class="pokemon-card" @click="selectPokemon">
    <div class="pokemon-nome">{{ pokemon.nome }}</div>
    <div class="pokemon-codigo">#{{ pokemon.codigo }}</div>
    <div class="tipos">
      <span
        class="tipo"
        :style="{ backgroundColor: tipoCorPrimario }"
      >
        {{ tipoPrimario }}
      </span>
      <span
        v-if="tipoSecundario"
        class="tipo"
        :style="{ backgroundColor: tipoCorSecundario }"
      >
        {{ tipoSecundario }}
      </span>
    </div>

    <div class="botoes">
      <button @click.stop="editPokemon">Editar</button>
      <button @click.stop="deletePokemon">Excluir</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PokemonCard',
  props: {
    pokemon: {
      type: Object,
      required: true
    }
  },
  computed: {
    tipoPrimario() {
      return this.pokemon.tipo_primario || this.pokemon.tipoPrimario;
    },
    tipoSecundario() {
      return this.pokemon.tipo_secundario || this.pokemon.tipoSecundario;
    },
    tipoCorPrimario() {
      return this.getTipoColor(this.tipoPrimario);
    },
    tipoCorSecundario() {
      return this.tipoSecundario ? this.getTipoColor(this.tipoSecundario) : null;
    }
  },
  methods: {
    getTipoColor(tipo) {
      const tipoLower = tipo?.toLowerCase() || '';
      const colorMap = {
        normal: '#A8A878',
        fire: '#F08030',
        water: '#6890F0',
        electric: '#F8D030',
        grass: '#78C850',
        ice: '#98D8D8',
        fighting: '#C03028',
        poison: '#A040A0',
        ground: '#E0C068',
        flying: '#A890F0',
        psychic: '#F85888',
        bug: '#A8B820',
        rock: '#B8A038',
        ghost: '#705898',
        dark: '#705848',
        dragon: '#7038F8',
        steel: '#B8B8D0',
        fairy: '#EE99AC'
      };
      return colorMap[tipoLower] || '#A8A878';
    },
    selectPokemon() {
      this.$emit('select', this.pokemon);
    },
    editPokemon() {
      this.$emit('edit', this.pokemon);
    },
    deletePokemon() {
      this.$emit('delete', this.pokemon.codigo);
    }
  }
}
</script>

<style scoped>
/* Card */
.pokemon-card {
  background: linear-gradient(135deg, rgba(230, 44, 44, 0.69) 0%, rgba(128, 121, 121, 0.9) 100%);
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 8px 20px rgba(255, 26, 26, 0.15);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  cursor: pointer
}

.pokemon-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 25px rgba(255, 26, 26, 0.25);
}


.pokemon-nome {
  color: #000;
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 5px;
}

.pokemon-codigo {
  color: #000;
  font-size: 14px;
  margin-bottom: 10px;
}

.tipos {
  margin-bottom: 10px;
}

.tipo {
  color: #000;
  background: #000000;
  border: 1px solid #000000;
  padding: 3px 8px;
  margin-right: 5px;
  border-radius: 3px;
  font-size: 12px;
  display: inline-block;
}

.botoes {
  display: flex;
  gap: 10px;
}

.botoes button {
  color: #000;
  background: #f0f0f0;
  border: 1px solid #000;
  border-radius: 25px;
  padding: 5px 10px;
  font-size: 12px;
  cursor: pointer;
}

.botoes button:first-child {
  background: #cda21b;
}

.botoes button:last-child {
  background: rgba(232, 47, 47, 0.9);
}
</style>
