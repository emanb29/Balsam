package me.ethanbell.balsam

import me.ethanbell.bitchunk.BitChunk
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import zio.DefaultRuntime

class MnemonicTest extends AnyFunSuite with Matchers {
  private def phraseFromHexString(hex: String) =
    getMnemonicForBits(BitChunk.fromHexString(hex), WordList.English)
  test("Mnemonic 128-bit simple vectors from trezor/python-mnemonic should hold") {
    phraseFromHexString("00000000000000000000000000000000").shouldEqual(
      "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about"
    )
    phraseFromHexString("7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f").shouldEqual(
      "legal winner thank year wave sausage worth useful legal winner thank yellow"
    )
    phraseFromHexString("80808080808080808080808080808080").shouldEqual(
      "letter advice cage absurd amount doctor acoustic avoid letter advice cage above"
    )
    phraseFromHexString("ffffffffffffffffffffffffffffffff").shouldEqual(
      "zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo wrong"
    )
  }
  test("Mnemonic 192-bit simple vectors from trezor/python-mnemonic should hold") {
    phraseFromHexString("000000000000000000000000000000000000000000000000").shouldEqual(
      "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon agent"
    )
    phraseFromHexString("7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f").shouldEqual(
      "legal winner thank year wave sausage worth useful legal winner thank year wave sausage worth useful legal will"
    )
    phraseFromHexString("808080808080808080808080808080808080808080808080").shouldEqual(
      "letter advice cage absurd amount doctor acoustic avoid letter advice cage absurd amount doctor acoustic avoid letter always"
    )
    phraseFromHexString("ffffffffffffffffffffffffffffffffffffffffffffffff").shouldEqual(
      "zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo when"
    )
  }
  test("Mnemonic 256-bit simple vectors from trezor/python-mnemonic should hold") {
    phraseFromHexString("0000000000000000000000000000000000000000000000000000000000000000")
      .shouldEqual(
        "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon art"
      )
    phraseFromHexString("7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f")
      .shouldEqual(
        "legal winner thank year wave sausage worth useful legal winner thank year wave sausage worth useful legal winner thank year wave sausage worth title"
      )
    phraseFromHexString("8080808080808080808080808080808080808080808080808080808080808080")
      .shouldEqual(
        "letter advice cage absurd amount doctor acoustic avoid letter advice cage absurd amount doctor acoustic avoid letter advice cage absurd amount doctor acoustic bless"
      )
    phraseFromHexString("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff")
      .shouldEqual(
        "zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo vote"
      )
  }
  test("Random-looking vectors from trezor/python-mnemonic should hold") {
    phraseFromHexString("9e885d952ad362caeb4efe34a8e91bd2").shouldEqual(
      "ozone drill grab fiber curtain grace pudding thank cruise elder eight picnic"
    )
    phraseFromHexString("6610b25967cdcca9d59875f5cb50b0ea75433311869e930b").shouldEqual(
      "gravity machine north sort system female filter attitude volume fold club stay feature office ecology stable narrow fog"
    )
    phraseFromHexString("68a79eaca2324873eacc50cb9c6eca8cc68ea5d936f98787c60c7ebc74e6ce7c")
      .shouldEqual(
        "hamster diagram private dutch cause delay private meat slide toddler razor book happy fancy gospel tennis maple dilemma loan word shrug inflict delay length"
      )
    phraseFromHexString("c0ba5a8e914111210f2bd131f3d5e08d").shouldEqual(
      "scheme spot photo card baby mountain device kick cradle pact join borrow"
    )
    phraseFromHexString("6d9be1ee6ebd27a258115aad99b7317b9c8d28b6d76431c3").shouldEqual(
      "horn tenant knee talent sponsor spell gate clip pulse soap slush warm silver nephew swap uncle crack brave"
    )
    phraseFromHexString("9f6a2878b2520799a44ef18bc7df394e7061a224d2c33cd015b157d746869863")
      .shouldEqual(
        "panda eyebrow bullet gorilla call smoke muffin taste mesh discover soft ostrich alcohol speed nation flash devote level hobby quick inner drive ghost inside"
      )
    phraseFromHexString("23db8160a31d3e0dca3688ed941adbf3").shouldEqual(
      "cat swing flag economy stadium alone churn speed unique patch report train"
    )
    phraseFromHexString("8197a4a47f0425faeaa69deebc05ca29c0a5b5cc76ceacc0").shouldEqual(
      "light rule cinnamon wrap drastic word pride squirrel upgrade then income fatal apart sustain crack supply proud access"
    )
    phraseFromHexString("066dca1a2bb7e8a1db2832148ce9933eea0f3ac9548d793112d9a95c9407efad")
      .shouldEqual(
        "all hour make first leader extend hole alien behind guard gospel lava path output census museum junior mass reopen famous sing advance salt reform"
      )
    phraseFromHexString("f30f8c1da665478f49b001d94c5fc452").shouldEqual(
      "vessel ladder alter error federal sibling chat ability sun glass valve picture"
    )
    phraseFromHexString("c10ec20dc3cd9f652c7fac2f1230f7a3c828389a14392f05").shouldEqual(
      "scissors invite lock maple supreme raw rapid void congress muscle digital elegant little brisk hair mango congress clump"
    )
    phraseFromHexString("f585c11aec520db57dd353c69554b21a89b20fb0650966fa0a9d6f74fd989d8f")
      .shouldEqual(
        "void come effort suffer camp survey warrior heavy shoot primary clutch crush open amazing screen patrol group space point ten exist slush involve unfold"
      )
  }
  test("Hash-based mnemonics should be consistent with iancoleman's implementation") {
    val (phrase, phrase12, phrase15, phrase18, phrase21, phrase24) =
      new DefaultRuntime {}.unsafeRun(for {
        entropy <- Entropy.fromBitChunk(
          BitChunk.fromHexString(
            "6ae05b7d1ae49175d85770d9c8002d5c22459600"
          )
        )
        phrase   <- Mnemonic.fromEntropy(entropy).phrase()
        phrase12 <- Mnemonic.fromEntropy(entropy).phrase(12)
        phrase15 <- Mnemonic.fromEntropy(entropy).phrase(15)
        phrase18 <- Mnemonic.fromEntropy(entropy).phrase(18)
        phrase21 <- Mnemonic.fromEntropy(entropy).phrase(21)
        phrase24 <- Mnemonic.fromEntropy(entropy).phrase(24)
      } yield {
        (phrase, phrase12, phrase15, phrase18, phrase21, phrase24)
      })
    phrase shouldEqual "helmet actress tent cupboard empower road genuine unlock supply divorce area reunion cattle slam absorb"
    phrase12 shouldEqual "surround science harvest clay inmate village state bless group flush digital case"
    phrase15 shouldEqual "surround science harvest clay inmate village state bless group flush digital case unveil latin roof"
    phrase18 shouldEqual "surround science harvest clay inmate village state bless group flush digital case unveil latin rookie spoon gate frost"
    phrase21 shouldEqual "surround science harvest clay inmate village state bless group flush digital case unveil latin rookie spoon gate forum smoke year crouch"
    phrase24 shouldEqual "surround science harvest clay inmate village state bless group flush digital case unveil latin rookie spoon gate forum smoke year creek stone cancel neutral"
  }

}
