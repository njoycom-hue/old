// 두뇌톡톡 - 치매예방 두뇌 게임 (초안)

const STORAGE_KEYS = {
  memory: 'bt_best_memory',
  sequence: 'bt_best_sequence',
  math: 'bt_best_math',
  word: 'bt_best_word',
};

const TITLES = {
  home: '두뇌톡톡',
  memory: '기억력 카드 짝맞추기',
  sequence: '순서 기억하기',
  math: '빠른 암산',
  word: '다른 것 찾기',
};

let currentGame = null;
let cleanupCurrentGame = null;

function $(sel) { return document.querySelector(sel); }
function $all(sel) { return Array.from(document.querySelectorAll(sel)); }

function getBest(game) {
  const raw = localStorage.getItem(STORAGE_KEYS[game]);
  return raw === null ? null : Number(raw);
}

function setBestIfBetter(game, value, higherIsBetter) {
  const best = getBest(game);
  if (best === null || (higherIsBetter ? value > best : value < best)) {
    localStorage.setItem(STORAGE_KEYS[game], String(value));
    return true;
  }
  return false;
}

function renderHomeBestScores() {
  const memoryBest = getBest('memory');
  const sequenceBest = getBest('sequence');
  const mathBest = getBest('math');
  const wordBest = getBest('word');
  $('[data-best="memory"]').textContent = memoryBest === null ? '최고 기록: -' : `최고 기록: ${memoryBest}회 만에 완료`;
  $('[data-best="sequence"]').textContent = sequenceBest === null ? '최고 기록: -' : `최고 기록: ${sequenceBest}라운드`;
  $('[data-best="math"]').textContent = mathBest === null ? '최고 기록: -' : `최고 기록: ${mathBest} / 10`;
  $('[data-best="word"]').textContent = wordBest === null ? '최고 기록: -' : `최고 기록: ${wordBest} / 10`;
}

function showScreen(name) {
  if (cleanupCurrentGame) {
    cleanupCurrentGame();
    cleanupCurrentGame = null;
  }
  currentGame = name;
  $all('.screen').forEach(el => el.classList.add('hidden'));
  $(`#screen-${name}`).classList.remove('hidden');
  $('#page-title').textContent = TITLES[name];
  $('#back-btn').classList.toggle('hidden', name === 'home');

  if (name === 'home') renderHomeBestScores();
  if (name === 'memory') startMemoryGame();
  if (name === 'sequence') resetSequenceGame();
  if (name === 'math') startMathGame();
  if (name === 'word') startWordGame();
}

$('#back-btn').addEventListener('click', () => showScreen('home'));
$all('.game-card').forEach(card => {
  card.addEventListener('click', () => showScreen(card.dataset.game));
});

// ---------- 기억력 카드 짝맞추기 ----------

const MEMORY_ICONS = ['🍎', '🍌', '🍇', '🍉', '🐶', '🐱'];

function shuffle(arr) {
  const a = arr.slice();
  for (let i = a.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [a[i], a[j]] = [a[j], a[i]];
  }
  return a;
}

function startMemoryGame() {
  const board = $('#memory-board');
  const icons = shuffle([...MEMORY_ICONS, ...MEMORY_ICONS]);
  let flipped = [];
  let matchedCount = 0;
  let moves = 0;
  let seconds = 0;
  let locked = false;

  $('#memory-moves').textContent = '시도 횟수: 0';
  $('#memory-timer').textContent = '시간: 0초';
  $('#memory-message').textContent = '';

  const timer = setInterval(() => {
    seconds++;
    $('#memory-timer').textContent = `시간: ${seconds}초`;
  }, 1000);

  board.innerHTML = '';
  icons.forEach((icon, index) => {
    const btn = document.createElement('button');
    btn.className = 'memory-card';
    btn.dataset.icon = icon;
    btn.dataset.index = String(index);
    btn.addEventListener('click', () => onCardClick(btn));
    board.appendChild(btn);
  });

  function onCardClick(btn) {
    if (locked) return;
    if (btn.classList.contains('flipped') || btn.classList.contains('matched')) return;

    btn.classList.add('flipped');
    btn.textContent = btn.dataset.icon;
    flipped.push(btn);

    if (flipped.length === 2) {
      moves++;
      $('#memory-moves').textContent = `시도 횟수: ${moves}`;
      locked = true;
      const [a, b] = flipped;
      if (a.dataset.icon === b.dataset.icon) {
        a.classList.add('matched');
        b.classList.add('matched');
        flipped = [];
        locked = false;
        matchedCount++;
        if (matchedCount === MEMORY_ICONS.length) {
          clearInterval(timer);
          const isRecord = setBestIfBetter('memory', moves, false);
          $('#memory-message').textContent = isRecord
            ? `축하합니다! ${moves}회 만에 완료했어요. 새로운 최고 기록!`
            : `잘하셨어요! ${moves}회 만에 완료했어요.`;
        }
      } else {
        setTimeout(() => {
          a.classList.remove('flipped');
          b.classList.remove('flipped');
          a.textContent = '';
          b.textContent = '';
          flipped = [];
          locked = false;
        }, 700);
      }
    }
  }

  cleanupCurrentGame = () => clearInterval(timer);
  $('#memory-restart').onclick = () => startMemoryGame();
}

// ---------- 순서 기억하기 (Simon) ----------

let seqState = null;

function resetSequenceGame() {
  seqState = { sequence: [], userIndex: 0, round: 0, playing: false, acceptingInput: false };
  $('#sequence-round').textContent = '라운드: 1';
  $('#sequence-best').textContent = `최고 기록: ${getBest('sequence') ?? 0}`;
  $('#sequence-message').textContent = '시작 버튼을 누르면 순서가 나와요';
  $('#sequence-start').textContent = '시작';
  $('#sequence-start').onclick = beginSequenceRound;

  $all('.seq-btn').forEach(btn => {
    btn.onclick = () => onSeqButtonClick(Number(btn.dataset.color));
  });

  cleanupCurrentGame = () => { seqState.playing = false; seqState.acceptingInput = false; };
}

function beginSequenceRound() {
  if (!seqState || seqState.playing) return;
  seqState.sequence.push(Math.floor(Math.random() * 4));
  seqState.round = seqState.sequence.length;
  seqState.userIndex = 0;
  $('#sequence-round').textContent = `라운드: ${seqState.round}`;
  $('#sequence-message').textContent = '순서를 잘 보세요...';
  $('#sequence-start').textContent = '진행 중...';
  playSequence();
}

function playSequence() {
  seqState.playing = true;
  seqState.acceptingInput = false;
  const buttons = $all('.seq-btn');
  let i = 0;
  const interval = setInterval(() => {
    buttons.forEach(b => b.classList.remove('lit'));
    if (i > 0) {
      const prevBtn = buttons[seqState.sequence[i - 1]];
      prevBtn.classList.remove('lit');
    }
    if (i >= seqState.sequence.length) {
      clearInterval(interval);
      buttons.forEach(b => b.classList.remove('lit'));
      seqState.playing = false;
      seqState.acceptingInput = true;
      $('#sequence-message').textContent = '이제 같은 순서로 눌러보세요';
      return;
    }
    buttons[seqState.sequence[i]].classList.add('lit');
    i++;
  }, 700);
}

function onSeqButtonClick(colorIndex) {
  if (!seqState || !seqState.acceptingInput) return;
  const btn = $all('.seq-btn')[colorIndex];
  btn.classList.add('lit');
  setTimeout(() => btn.classList.remove('lit'), 250);

  const expected = seqState.sequence[seqState.userIndex];
  if (colorIndex !== expected) {
    seqState.acceptingInput = false;
    const isRecord = setBestIfBetter('sequence', seqState.round - 1, true);
    $('#sequence-message').textContent = seqState.round > 1
      ? `아쉬워요! ${seqState.round - 1}라운드까지 성공했어요.${isRecord ? ' 새로운 최고 기록!' : ''}`
      : '아쉬워요! 다시 도전해보세요.';
    $('#sequence-best').textContent = `최고 기록: ${getBest('sequence') ?? 0}`;
    $('#sequence-start').textContent = '다시 시작';
    $('#sequence-start').onclick = () => { resetSequenceGame(); };
    return;
  }

  seqState.userIndex++;
  if (seqState.userIndex === seqState.sequence.length) {
    seqState.acceptingInput = false;
    $('#sequence-message').textContent = '성공! 다음 라운드로 이어집니다';
    setTimeout(beginSequenceRound, 900);
  }
}

// ---------- 빠른 암산 ----------

const MATH_TOTAL = 10;
let mathState = null;

function generateMathQuestion() {
  const useSubtraction = Math.random() < 0.5;
  let a = Math.floor(Math.random() * 20) + 1;
  let b = Math.floor(Math.random() * 20) + 1;
  let answer, text;
  if (useSubtraction) {
    if (b > a) [a, b] = [b, a];
    answer = a - b;
    text = `${a} - ${b} = ?`;
  } else {
    answer = a + b;
    text = `${a} + ${b} = ?`;
  }
  const choices = new Set([answer]);
  while (choices.size < 4) {
    const delta = Math.floor(Math.random() * 9) - 4;
    const candidate = answer + delta;
    if (candidate >= 0 && candidate !== answer) choices.add(candidate);
  }
  return { text, answer, choices: shuffle(Array.from(choices)) };
}

function startMathGame() {
  mathState = { index: 0, score: 0, locked: false };
  $('#math-result').textContent = '';
  nextMathQuestion();
  cleanupCurrentGame = () => { mathState = null; };
}

function nextMathQuestion() {
  if (!mathState) return;
  if (mathState.index >= MATH_TOTAL) {
    const isRecord = setBestIfBetter('math', mathState.score, true);
    $('#math-question').textContent = `완료! ${mathState.score} / ${MATH_TOTAL}`;
    $('#math-choices').innerHTML = '';
    $('#math-result').textContent = isRecord ? '새로운 최고 기록이에요!' : '수고하셨어요!';
    return;
  }
  const q = generateMathQuestion();
  mathState.current = q;
  mathState.locked = false;
  $('#math-progress').textContent = `문제: ${mathState.index + 1} / ${MATH_TOTAL}`;
  $('#math-score').textContent = `맞은 개수: ${mathState.score}`;
  $('#math-question').textContent = q.text;
  $('#math-result').textContent = '';
  const container = $('#math-choices');
  container.innerHTML = '';
  q.choices.forEach(choice => {
    const btn = document.createElement('button');
    btn.className = 'choice-btn';
    btn.textContent = String(choice);
    btn.onclick = () => onMathChoice(btn, choice);
    container.appendChild(btn);
  });
}

function onMathChoice(btn, choice) {
  if (!mathState || mathState.locked) return;
  mathState.locked = true;
  const correct = choice === mathState.current.answer;
  $all('#math-choices .choice-btn').forEach(b => {
    b.disabled = true;
    if (Number(b.textContent) === mathState.current.answer) b.classList.add('correct');
    else if (b === btn) b.classList.add('wrong');
  });
  if (correct) {
    mathState.score++;
    $('#math-result').textContent = '정답이에요!';
  } else {
    $('#math-result').textContent = `아쉬워요. 정답은 ${mathState.current.answer}이에요.`;
  }
  mathState.index++;
  setTimeout(nextMathQuestion, 1000);
}

// ---------- 다른 것 찾기 ----------

const WORD_CATEGORIES = [
  { name: '과일', words: ['사과', '바나나', '포도', '딸기', '수박', '오렌지'] },
  { name: '동물', words: ['호랑이', '사자', '코끼리', '토끼', '강아지', '고양이'] },
  { name: '채소', words: ['당근', '오이', '감자', '양파', '배추'] },
  { name: '색깔', words: ['빨강', '파랑', '노랑', '초록', '보라'] },
  { name: '가구', words: ['침대', '소파', '책상', '의자', '옷장'] },
  { name: '교통수단', words: ['자동차', '버스', '기차', '비행기', '자전거'] },
  { name: '악기', words: ['피아노', '기타', '바이올린', '드럼', '플루트'] },
];

const WORD_TOTAL = 10;
let wordState = null;

function generateWordQuestion() {
  const catIndex = Math.floor(Math.random() * WORD_CATEGORIES.length);
  const mainCategory = WORD_CATEGORIES[catIndex];
  const otherIndex = (catIndex + 1 + Math.floor(Math.random() * (WORD_CATEGORIES.length - 1))) % WORD_CATEGORIES.length;
  const oddCategory = WORD_CATEGORIES[otherIndex];

  const mainWords = shuffle(mainCategory.words).slice(0, 3);
  const oddWord = shuffle(oddCategory.words)[0];

  return { choices: shuffle([...mainWords, oddWord]), answer: oddWord };
}

function startWordGame() {
  wordState = { index: 0, score: 0, locked: false };
  $('#word-result').textContent = '';
  nextWordQuestion();
  cleanupCurrentGame = () => { wordState = null; };
}

function nextWordQuestion() {
  if (!wordState) return;
  if (wordState.index >= WORD_TOTAL) {
    const isRecord = setBestIfBetter('word', wordState.score, true);
    $('#word-progress').textContent = `완료! ${wordState.score} / ${WORD_TOTAL}`;
    $('#word-choices').innerHTML = '';
    $('#word-result').textContent = isRecord ? '새로운 최고 기록이에요!' : '수고하셨어요!';
    return;
  }
  const q = generateWordQuestion();
  wordState.current = q;
  wordState.locked = false;
  $('#word-progress').textContent = `문제: ${wordState.index + 1} / ${WORD_TOTAL}`;
  $('#word-score').textContent = `맞은 개수: ${wordState.score}`;
  $('#word-result').textContent = '';
  const container = $('#word-choices');
  container.innerHTML = '';
  q.choices.forEach(choice => {
    const btn = document.createElement('button');
    btn.className = 'choice-btn';
    btn.textContent = choice;
    btn.onclick = () => onWordChoice(btn, choice);
    container.appendChild(btn);
  });
}

function onWordChoice(btn, choice) {
  if (!wordState || wordState.locked) return;
  wordState.locked = true;
  const correct = choice === wordState.current.answer;
  $all('#word-choices .choice-btn').forEach(b => {
    b.disabled = true;
    if (b.textContent === wordState.current.answer) b.classList.add('correct');
    else if (b === btn) b.classList.add('wrong');
  });
  $('#word-result').textContent = correct ? '정답이에요!' : `아쉬워요. 정답은 "${wordState.current.answer}"이에요.`;
  wordState.index++;
  setTimeout(nextWordQuestion, 1000);
}

// ---------- 초기화 ----------

showScreen('home');
